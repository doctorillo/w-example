import { connect } from 'react-redux'
import { createSelector } from 'reselect'
import {
  plannerExcursionAddVariant,
  plannerExcursionDateUpdate,
  plannerExcursionFilterReset, plannerExcursionFilterSetDates,
  plannerExcursionFilterSetName,
  plannerExcursionFilterSetPrice, plannerExcursionFilterSetTags,
  plannerExcursionSetMaxItems,
} from '../trip-planner/actions'
import { STATE_SEARCH } from './reducers'
import { EnumUI } from '../../../types/basic/EnumUI'
import { STATE_TRIP_PLANNER } from '../trip-planner/reducers'
import { Nullable } from '../../../types/Nullable'
import { RootState } from '../../index'
import { Dispatch } from 'redux'
import {
  plannerBasicSessionAsk,
  plannerExcursionSessionAsk,
  plannerSelectedAsk,
} from '../../../types/planner/PlannerSession'
import { AppProps } from '../env/connect'
import { EitherEnv, fold, makeLeft, makeRight } from '../../../types/EitherEnv'
import { ResultKind } from '../../../types/ResultKind'
import { ExcursionFilterParams } from '../../../types/planner/ExcursionFilterParams'
import { ExcursionCardUI, orderByDate } from '../../../types/bookings/ExcursionCardUI'
import { filterExcursion } from '../../../types/planner/PlannerExcursionFilterFn'
import { Uuid } from '../../../types/basic/Uuid'
import { DateString } from '../../../types/basic/DateString'
import { ExcursionTagItem } from '../../../types/bookings/ExcursionTagItem'
import { STATE_EXCURSION_TAG } from '../excursion-tag/reducers'
import { PlannerExcursionItem } from '../../../types/planner/PlannerExcursionItem'

export interface CardEnv {
  card: ExcursionCardUI;
  selected: DateString;
  inCard: boolean;
}

export interface SearchExcursionEnv {
  plannerId: Uuid;
  plannerPointId: Uuid;
  clients: Uuid[];
  filterParams: ExcursionFilterParams;
  items: CardEnv[];
  excursionDates: DateString[];
  filteredCount: number;
  allCount: number;
  price: number[];
  tags: EnumUI[];
  maxItems: number;
  status: ResultKind;
}

const tripValue = (state: RootState): STATE_TRIP_PLANNER => state.sliceTripPlanner
const tagValue = (state: RootState): STATE_EXCURSION_TAG => state.sliceExcursionTag
const searchValue = (state: RootState): STATE_SEARCH => state.sliceExcursionSearch
const getProps = () =>
  createSelector(
    tripValue,
    tagValue,
    searchValue,
    (
      tripState,
      excursionTagState,
      state
    ): EitherEnv<ResultKind, SearchExcursionEnv> => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(tripState))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(tripState))
      if (!basicAsk || !excursionAsk){
        return makeLeft(state.status)
      }
      const { clients } = basicAsk
      const { excursionSelectedDates, excursionClientItems, excursionFilter } = excursionAsk
      const price: number[] = excursionFilter.price
      const filtered: ExcursionCardUI[] = filterExcursion(state.data, excursionSelectedDates, excursionFilter)
      const dateSort = orderByDate(excursionSelectedDates)
      const items = (filtered.length === 0 ? [] : filtered.slice().filter(x => x.accommodationPax <= clients.length).sort(dateSort)).map(x => {
        const ed = excursionSelectedDates.find(z => z.excursionOfferId === x.id) || null
        if (!ed) {
          return null
        }
        const ei = excursionClientItems.find(z => z.date === ed.selected && !!z.variants.find(y => y.excursionId === x.excursionId))
        return {
          card: x,
          selected: ed.selected,
          inCard: !!ei
        } as CardEnv
      }).reduce((a, x) => {
        if (!x){
          return a
        }
        return [...a, x]
      }, [] as CardEnv[])
      const tags = items.reduce((a, x) => {
        const t = x.card.tags.filter(z => !a.includes(z))
        return [...a, ...t]
      }, [] as ExcursionTagItem[]).reduce((a, x) => {
        const probe = excursionTagState.data.find(z => z.value === x)
        if (!probe){
          return a
        }
        return [...a, probe]
      }, [] as EnumUI[])
      return makeRight({
        plannerId: basicAsk.session.id,
        plannerPointId: basicAsk.point.id,
        clients: clients.map(x => x.id),
        filterParams: excursionFilter,
        items: items,
        excursionDates: excursionAsk.excursionDates,
        excursionSelectedDates: excursionAsk.excursionSelectedDates,
        filteredCount: items.length,
        allCount: state.count,
        price: price,
        tags: tags,
        maxItems: excursionAsk.excursionMaxItems,
        status: state.status
      })
    }
  )

const mapStateToProps = (state: RootState): EitherEnv<ResultKind, SearchExcursionEnv> => getProps()(state)

export interface SearchExcursionFn {
  toExcursion(excursionId: Uuid, supplierId: Uuid, customerId: Uuid): void;

  filterName(name: string | null): void;

  filterPrice(items: number[]): void;

  filterTags(items: number[]): void;

  filterDates(items: DateString[]): void;

  filterReset(): void;

  setMaxItems(items: number): void;

  selectDate(excursionOfferId: Uuid, selected: DateString): void;

  addExcursionToPlan(
    plannerId: Uuid,
    plannerPointId: Uuid,
    plannerClients: Uuid[],
    item: PlannerExcursionItem,
  ): void;
}

const mapDispatchToProps = (dispatch: Dispatch): SearchExcursionFn => {
  return {
    toExcursion(
      excursionId: Uuid,
      supplierId: Uuid,
      customerId: Uuid
    ): void {
      console.log(`${excursionId}. ${supplierId}. ${customerId}`)
    },
    filterName(name: Nullable<string>): void {
      dispatch(plannerExcursionFilterSetName(name))
    },
    filterPrice(items: number[]): void {
      dispatch(plannerExcursionFilterSetPrice(items))
    },
    filterTags(items: number[]): void {
      dispatch(plannerExcursionFilterSetTags(items))
    },
    filterDates(items: DateString[]): void {
      dispatch(plannerExcursionFilterSetDates(items))
    },
    setMaxItems(items: number): void {
      dispatch(plannerExcursionSetMaxItems(items))
    },
    filterReset(): void {
      dispatch(plannerExcursionFilterReset())
    },
    selectDate(excursionOfferId: Uuid, selected: DateString): void {
      dispatch(plannerExcursionDateUpdate({
        excursionOfferId,
        selected
      }))
    },
    addExcursionToPlan(
      plannerId: Uuid,
      plannerPointId: Uuid,
      plannerClients: Uuid[],
      item: PlannerExcursionItem,
    ): void {
      dispatch(plannerExcursionAddVariant({
        plannerId,
        clientId: plannerClients,
        plannerPointId: plannerPointId,
        item
      }))
    },
  }
}

export type SearchExcursionProps = {
  appProps: AppProps;
  env: Nullable<SearchExcursionEnv>;
  fn: SearchExcursionFn;
  status: ResultKind;
}

const mergeProps = (
  stateProps: EitherEnv<ResultKind, SearchExcursionEnv>,
  dispatchProps: SearchExcursionFn,
  ownProps: AppProps
): SearchExcursionProps => fold(stateProps, l => ({
  appProps: ownProps,
  env: null as Nullable<SearchExcursionEnv>,
  fn: dispatchProps,
  status: l
}), r => ({
  appProps: ownProps,
  env: r,
  fn: dispatchProps,
  status: r.status
}))

const make = (comp: any) =>
  connect(
    mapStateToProps,
    mapDispatchToProps,
    mergeProps
  )(comp)

export default make
