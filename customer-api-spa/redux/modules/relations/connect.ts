import { PartyValue } from '../../../types/parties/PartyValue'
import { ResultKind } from '../../../types/ResultKind'
import { RootState } from '../../index'
import { createSelector } from 'reselect'
import { EitherEnv, fold, makeLeft, makeRight } from '../../../types/EitherEnv'
import { Dispatch } from 'redux'
import { Nullable } from '../../../types/Nullable'
import { AppProps } from '../env/connect'
import { connect } from 'react-redux'
import { STATE_RELATIONS } from './reducers'
import { relationFetch } from './actions'
import { ContextItem } from '../../../types/basic/ContextItem'
import { RelationSelectProps } from '../../../components/trip-planner/search/form/selects/relations'

export interface RelationEnv {
  customers: PartyValue[];
  status: ResultKind;
}

export interface RelationFn {
  fetch(partyId: string, context: ContextItem): void;
}

const stateRelation: (state: RootState) => STATE_RELATIONS = (state: RootState) => (state.sliceRelation)

const getProps = () =>
  createSelector(
    stateRelation,
    (state: STATE_RELATIONS): EitherEnv<ResultKind, RelationEnv> => {
      if (state.status !== ResultKind.Completed || !state.item) {
        return makeLeft(state.status)
      }
      return makeRight({
        customers: state.item.customers,
        status: state.status,
      })
    },
  )

const mapStateToProps = (state: RootState): EitherEnv<ResultKind, RelationEnv> => getProps()(state)

const mapDispatchToProps = (dispatch: Dispatch): RelationFn => ({
  fetch(partyId: string, context: ContextItem): void {
    dispatch(relationFetch({
      partyId: partyId,
      ctx: context,
    }))
  },
})

export type RelationPageProps = {
  appProps: AppProps & RelationSelectProps;
  env: Nullable<RelationEnv>;
  fn: RelationFn;
  status: ResultKind;
}

const mergeProps = (
  stateProps: EitherEnv<ResultKind, RelationEnv>,
  dispatchProps: RelationFn,
  ownProps: AppProps & RelationSelectProps,
): RelationPageProps => fold(stateProps, l => ({
  appProps: ownProps,
  env: null as Nullable<RelationEnv>,
  fn: dispatchProps,
  status: l
}), r => ({
  appProps: ownProps,
  env: r,
  fn: dispatchProps,
  status: r.status
}))

export default (comp: any) =>
  connect(
    mapStateToProps,
    mapDispatchToProps,
    mergeProps,
  )(comp)