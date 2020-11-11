import { connect } from 'react-redux'
import { createSelector } from 'reselect'
import {
  plannerPropertyFilterReset,
  plannerPropertyFilterSetAmenities,
  plannerPropertyFilterSetBoardings,
  plannerPropertyFilterSetFacilities,
  plannerPropertyFilterSetIndications,
  plannerPropertyFilterSetMedicals,
  plannerPropertyFilterSetName,
  plannerPropertyFilterSetPrice,
  plannerPropertyFilterSetStar,
  plannerPropertyFilterSetStop,
  plannerPropertyFilterSetTherapies,
  plannerPropertySetMaxItems,
  plannerPropertySetPriceMode,
} from '../trip-planner/actions'
import { STATE_SEARCH } from './reducers'
import { PropertyCardUI } from '../../../types/bookings/PropertyCardUI'
import { EnumUI } from '../../../types/basic/EnumUI'
import { STATE_AMENITY } from '../property-extras/amenities/reducers'
import { STATE_FACILITY } from '../property-extras/facilities/reducers'
import { STATE_THERAPY } from '../property-extras/therapies/reducers'
import { PriceViewMode } from '../../../types/PriceViewMode'
import { BoardingUI } from '../../../types/bookings/BoardingUI'
import { propertySet } from '../property/actions'
import { STATE_TRIP_PLANNER } from '../trip-planner/reducers'
import { PropertyFilterParams } from '../../../types/planner/PropertyFilterParams'
import { filterProperty } from '../../../types/planner/PlannerPropertyFilterFn'
import { Nullable } from '../../../types/Nullable'
import { RootState } from '../../index'
import { Dispatch } from 'redux'
import { plannerSelectedAsk, plannerPropertySessionAsk } from '../../../types/planner/PlannerSession'
import { AppProps } from '../env/connect'
import { EitherEnv, fold, makeLeft, makeRight } from '../../../types/EitherEnv'
import { ResultKind } from '../../../types/ResultKind'
import { STATE_INDICATION } from '../property-extras/indications/reducers'

export interface SearchPropertyEnv {
  filterParams: PropertyFilterParams;
  items: PropertyCardUI[];
  filteredCount: number;
  allCount: number;
  stars: number[];
  price: number[];
  boardings: BoardingUI[];
  amenities: EnumUI[];
  facilities: EnumUI[];
  medicals: EnumUI[];
  indications: EnumUI[];
  therapies: EnumUI[];
  priceViewMode: PriceViewMode;
  maxItems: number;
  status: ResultKind;
}

const tripValue = (state: RootState): STATE_TRIP_PLANNER => state.sliceTripPlanner
const amenityValue = (state: RootState): STATE_AMENITY => state.sliceAmenities
const facilityValue = (state: RootState): STATE_FACILITY => state.sliceFacilities
const therapyValue = (state: RootState): STATE_THERAPY => state.sliceTherapies
const indicationValue = (state: RootState): STATE_INDICATION => state.sliceIndications
const searchValue = (state: RootState): STATE_SEARCH => state.slicePropertySearch
const getProps = () =>
  createSelector(
    tripValue,
    amenityValue,
    facilityValue,
    therapyValue,
    indicationValue,
    searchValue,
    (
      tripState,
      amenityState,
      facilityState,
      therapyState,
      indicationState,
      state
    ): EitherEnv<ResultKind, SearchPropertyEnv> => {
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(tripState))
      if (!propertyAsk){
        return makeLeft(state.status)
      }
      const filterOps = propertyAsk.propertyFilter
      const stars: number[] = filterOps.stars
      const price: number[] = filterOps.price
      const items: PropertyCardUI[] = filterProperty(state.data, filterOps)
      return makeRight({
        filterParams: filterOps,
        items: items,
        filteredCount: items.length,
        allCount: state.count,
        stars: stars,
        price: price,
        boardings: items.reduce((acc: BoardingUI[], x: PropertyCardUI) => {
          const xb: BoardingUI[] = x.boardings
          return [
            ...acc,
            ...xb.filter(
              (z: BoardingUI) =>
                acc.filter((y: BoardingUI) => z.id === y.id).length === 0
            ),
          ]
        }, []),
        amenities: amenityState.data.filter(
          (x: EnumUI) =>
            items.filter((z: PropertyCardUI) => z.amenities.includes(x.id))
              .length > 0
        ),
        facilities: facilityState.data.filter(
          (x: EnumUI) =>
            items.filter((z: PropertyCardUI) => z.facilities.includes(x.id))
              .length > 0
        ),
        medicals: [],
        indications: indicationState.data.filter(
          (x: EnumUI) =>
            items.filter((z: PropertyCardUI) => z.indications.includes(x.id))
              .length > 0
        ),
        therapies: therapyState.data.filter(
          (x: EnumUI) =>
            items.filter((z: PropertyCardUI) => z.therapies.includes(x.id))
              .length > 0
        ),
        priceViewMode: propertyAsk.propertyPriceView,
        maxItems: propertyAsk.propertyMaxItems,
        status: state.status
      })
    }
  )

const mapStateToProps = (state: RootState): EitherEnv<ResultKind, SearchPropertyEnv> => getProps()(state)

export interface SearchPropertyFn {
  toProperty(propertyId: string, supplierId: string, customerId: string): void;

  filterStop(stop: boolean): void;

  filterStar(stars: number[]): void;

  filterName(name: string | null): void;

  filterPrice(items: number[]): void;

  filterBoardings(items: string[]): void;

  filterAmenities(items: string[]): void;

  filterFacilities(items: string[]): void;

  filterMedicals(items: string[]): void;

  filterIndications(items: string[]): void;

  filterTherapies(items: string[]): void;

  filterReset(): void;

  setMaxItems(items: number): void;

  setPriceView(mode: PriceViewMode): void;
}

const mapDispatchToProps = (dispatch: Dispatch): SearchPropertyFn => {
  return {
    toProperty(
      propertyId: string,
      supplierId: string,
      customerId: string
    ): void {
      dispatch(
        propertySet({
          propertyId,
          supplierId,
          customerId,
        })
      )
    },
    filterStop(stop: boolean): void {
      dispatch(plannerPropertyFilterSetStop(stop))
    },
    filterStar(stars: number[]): void {
      dispatch(plannerPropertyFilterSetStar(stars))
    },
    filterName(name: Nullable<string>): void {
      dispatch(plannerPropertyFilterSetName(name))
    },
    filterPrice(items: number[]): void {
      dispatch(plannerPropertyFilterSetPrice(items))
    },
    filterBoardings(items: string[]): void {
      dispatch(plannerPropertyFilterSetBoardings(items))
    },
    filterAmenities(items: string[]): void {
      dispatch(plannerPropertyFilterSetAmenities(items))
    },
    filterFacilities(items: string[]): void {
      dispatch(plannerPropertyFilterSetFacilities(items))
    },
    filterMedicals(items: string[]): void {
      dispatch(plannerPropertyFilterSetMedicals(items))
    },
    filterIndications(items: string[]): void {
      dispatch(plannerPropertyFilterSetIndications(items))
    },
    filterTherapies(items: string[]): void {
      dispatch(plannerPropertyFilterSetTherapies(items))
    },
    setMaxItems(items: number): void {
      dispatch(plannerPropertySetMaxItems(items))
    },
    setPriceView(mode: PriceViewMode): void {
      dispatch(plannerPropertySetPriceMode(mode))
    },
    filterReset(): void {
      dispatch(plannerPropertyFilterReset())
    },
  }
}

export type SearchPropertyProps = {
  appProps: AppProps;
  env: Nullable<SearchPropertyEnv>;
  fn: SearchPropertyFn;
  status: ResultKind;
}

const mergeProps = (
  stateProps: EitherEnv<ResultKind, SearchPropertyEnv>,
  dispatchProps: SearchPropertyFn,
  ownProps: AppProps
): SearchPropertyProps => fold(stateProps, l => ({
  appProps: ownProps,
  env: null as Nullable<SearchPropertyEnv>,
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
