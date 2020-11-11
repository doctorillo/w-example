import { connect } from 'react-redux'
import { createSelector } from 'reselect'
import { PointOption, toPointOption } from '../../../types/geo/PointOption'
import { Nullable } from '../../../types/Nullable'
import { RootState } from '../../index'
import { STATE_POINT } from './reducers'
import { ResultKind } from '../../../types/ResultKind'
import { Dispatch } from 'redux'
import { pointFetch, pointSelect } from './actions'
import { AppProps } from '../env/connect'
import { EitherEnv, fold, makeLeft, makeRight } from '../../../types/EitherEnv'

export interface CitySelectProps {
  selectedPoint: Nullable<PointOption>;

  selectPoint(point: Nullable<PointOption>): void;
}

export interface PointEnv {
  points: PointOption[];
  selected: Nullable<PointOption>;
  status: ResultKind;
}

export interface PointFn {
  fetch(): void;

  select(id: Nullable<string>): void;
}

const statePoint: (state: RootState) => STATE_POINT = (state: RootState) => (state.slicePoints)

const getProps = () =>
  createSelector(
    statePoint,
    (sp: STATE_POINT): EitherEnv<ResultKind, PointEnv> => {
      if (sp.status !== ResultKind.Completed) {
        return makeLeft(sp.status)
      }
      const points = sp.internal.filter(x => x.id !== sp.extraEnv?.selected).map(x => ({
        value: x.id,
        category: x.category,
        label: x.label.label,
      }))
      const selected = sp.internal.find(x => x.id === sp?.extraEnv?.selected)
      if (selected) {
        return makeRight({
          points,
          selected: toPointOption(selected),
          status: sp.status,
        })
      }
      return makeRight({
        points,
        selected: null,
        status: sp.status,
      })
    },
  )

const mapStateToProps = (state: RootState): EitherEnv<ResultKind, PointEnv> => getProps()(state)

const mapDispatchToProps = (dispatch: Dispatch): PointFn => ({
  fetch(): void {
    dispatch(pointFetch())
  },
  select(id: Nullable<string>): void {
    dispatch(pointSelect(id))
  },
})

export type PointProps = {
  appProps: AppProps;
  env: Nullable<PointEnv>;
  fn: PointFn;
  pointSelect: CitySelectProps;
  status: ResultKind;
}

const mergeProps = (
  stateProps: EitherEnv<ResultKind, PointEnv>,
  dispatchProps: PointFn,
  ownProps: AppProps & CitySelectProps,
): PointProps => fold(stateProps, l => ({
  appProps: { appEnv: ownProps.appEnv, appFn: ownProps.appFn },
  env: null as Nullable<PointEnv>,
  fn: dispatchProps,
  pointSelect: { selectedPoint: ownProps.selectedPoint, selectPoint: ownProps.selectPoint },
  status: l,
}), r => ({
  appProps: { appEnv: ownProps.appEnv, appFn: ownProps.appFn },
  env: r,
  fn: dispatchProps,
  pointSelect: { selectedPoint: ownProps.selectedPoint, selectPoint: ownProps.selectPoint },
  status: r.status,
}))

export default (comp: any) =>
  connect(
    mapStateToProps,
    mapDispatchToProps,
    mergeProps,
  )(comp)