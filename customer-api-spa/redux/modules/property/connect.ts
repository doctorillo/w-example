import { connect } from 'react-redux'
import { ResultKind } from '../../../types/ResultKind'
import { propertyDescriptionFetch, propertyPriceFetch, propertySet } from './actions'
import { DateRange } from '../../../types/DateRange'
import { PriceUnitUI } from '../../../types/property/prices/PriceUnitUI'
import { PropertyCardUI } from '../../../types/bookings/PropertyCardUI'
import { PropertyPoint, toPoint } from '../../../types/property/PropertyPoint'
import { PropertyDescriptionUI } from '../../../types/property/PropertyDescriptionUI'
import { PlannerAccommodationItem } from '../../../types/planner/PlannerAccommodationItem'
import { PlannerClient } from '../../../types/planner/PlannerClient'
import { PlannerRoom } from '../../../types/planner/PlannerRoom'
import { plannerPropertyAddVariant } from '../trip-planner/actions'
import { Nullable } from '../../../types/Nullable'
import { Dispatch } from 'redux'
import { AppProps } from '../env/connect'
import { EitherEnv, fold, makeLeft, makeRight } from '../../../types/EitherEnv'
import { RootState } from '../../index'
import { createSelector } from 'reselect'
import { STATE_ENV } from '../env/reducers'
import { STATE_TRIP_PLANNER } from '../trip-planner/reducers'
import { STATE_AMENITY } from '../property-extras/amenities/reducers'
import { STATE_SEARCH } from '../property-search/reducers'
import { STATE_PROPERTY } from './reducers'
import {
  plannerBasicSessionAsk,
  plannerPropertySessionAsk,
  plannerSelectedAsk,
} from '../../../types/planner/PlannerSession'
import { envAsk } from '../../../types/contexts/EnvState'
import { EnumUI } from '../../../types/basic/EnumUI'

export interface GroupPrice {
  roomId: string;
  roomOrder: number;
  priceUnitId: string;
}

export interface PropertyEnv {
  plannerId: string;
  plannerPointId: string;
  plannerPropertyId: string;
  plannerClients: PlannerClient[];
  rooms: PlannerRoom[];
  dates: DateRange;
  propertyId: string;
  card: PropertyCardUI;
  infrastructure: string[];
  description: Nullable<PropertyDescriptionUI>;
  descriptionStatus: ResultKind;
  prices: PriceUnitUI[];
  pricesInCart: GroupPrice[];
  priceStatus: ResultKind;
  point: PropertyPoint;
  otherPoints: PropertyPoint[];
  status: ResultKind;
}

export interface AddToPlanFn {
  addRoomVariantToPlan(
    plannerId: string,
    plannerPointId: string,
    plannerPropertyId: string,
    plannerRoomId: string,
    item: PlannerAccommodationItem,
  ): void;
}

export interface PropertyFn {
  fetchPropertyData(): void;

  reloadPropertyData(): void;

  toProperty(propertyId: string, supplierId: string, customerId: string): void;
}

const envValue = (state: RootState) => state.sliceEnv
const plannerValue = (state: RootState) => state.sliceTripPlanner
const amenityValue = (state: RootState) => state.sliceAmenities
const searchValue = (state: RootState) => state.slicePropertySearch
const propertyValue = (state: RootState) => state.sliceProperty

const getProps = () =>
  createSelector(
    envValue,
    plannerValue,
    amenityValue,
    searchValue,
    propertyValue,
    (
      appEnv: STATE_ENV,
      tripState: STATE_TRIP_PLANNER,
      amenityState: STATE_AMENITY,
      searchState: STATE_SEARCH,
      state: STATE_PROPERTY
    ): EitherEnv<ResultKind, PropertyEnv> => {
      if (state.priceStatus !== ResultKind.Completed){
        return makeLeft(state.priceStatus)
      }
      const env = envAsk(appEnv)
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(tripState))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(tripState))
      if (!propertyAsk || !env || !basicAsk){
        return makeLeft(state.priceStatus)
      }
      const customerId = env.customerId
      const card = searchState.data.find((x: PropertyCardUI) => state.id === x.id)
      if (!card){
        return makeLeft(state.priceStatus)
      }
      const infrastructure =
        (card &&
          amenityState.data
            .filter((x: EnumUI) => card.amenities.indexOf(x.id) > -1)
            .map((x: EnumUI) => x.label.label)) ||
        []
      const actual = toPoint(card, customerId, true)
      const otherPoints = searchState.data
          .filter((x: PropertyCardUI) => card.id !== x.id)
          .map(x => toPoint(x, customerId))
      const pricesInCart: GroupPrice[] = propertyAsk.propertyRooms.reduce(
        (acc: GroupPrice[], x: PlannerRoom) => {
          return [
            ...acc,
            ...x.variants.map((z: PlannerAccommodationItem) => ({
              roomId: x.id,
              roomOrder: x.position,
              priceUnitId: z.roomPriceUnitId,
            })),
          ]
        },
        []
      )
      const pEnv: PropertyEnv = {
        plannerId: basicAsk.session.id,
        plannerPointId: basicAsk.point.id,
        plannerPropertyId: propertyAsk.propertyId,
        plannerClients: basicAsk.clients,
        rooms: propertyAsk.propertyRooms,
        dates: basicAsk.point.dates,
        propertyId: state.id,
        card: card,
        infrastructure: infrastructure,
        description: state.description,
        descriptionStatus: state.descriptionStatus,
        prices: state.prices,
        pricesInCart: pricesInCart,
        priceStatus: state.priceStatus,
        point: actual,
        otherPoints: otherPoints || [],
        status: state.priceStatus
      }
      return makeRight(pEnv)
    }
  )

const mapStateToProps = (state: RootState): EitherEnv<ResultKind, PropertyEnv> => getProps()(state)

const mapDispatchToProps = (dispatch: Dispatch): PropertyFn & AddToPlanFn => {
  return {
    fetchPropertyData(): void {
      dispatch(propertyDescriptionFetch())
      dispatch(propertyPriceFetch())
    },
    reloadPropertyData(): void {
      dispatch(propertyDescriptionFetch())
      dispatch(propertyPriceFetch())
    },
    toProperty(
      propertyId: string,
      supplierId: string,
      customerId: string,
    ): void {
      console.log('to property')
      dispatch(
        propertySet({
          propertyId,
          supplierId,
          customerId,
        }),
      )
    },
    addRoomVariantToPlan(
      plannerId: string,
      plannerPointId: string,
      plannerPropertyId: string,
      plannerRoomId: string,
      room: PlannerAccommodationItem,
    ): void {
      dispatch(
        plannerPropertyAddVariant({
          plannerId,
          plannerPointId,
          plannerPropertyId,
          plannerRoomId,
          room,
        }),
      )
    },
  }
}

export type PropertyPageProps = {
  appProps: AppProps;
  propertyProps: Nullable<PropertyEnv>;
  propertyFn: PropertyFn & AddToPlanFn;
  status: ResultKind;
}

const mergeProps = (
  stateProps: EitherEnv<ResultKind, PropertyEnv>,
  dispatchProps: PropertyFn & AddToPlanFn,
  ownProps: AppProps,
): PropertyPageProps => fold(stateProps, l => ({
  appProps: ownProps,
  propertyProps: null as Nullable<PropertyEnv>,
  propertyFn: dispatchProps,
  status: l,
}), r => ({
  appProps: ownProps,
  propertyProps: r,
  propertyFn: dispatchProps,
  status: r.status,
}))

const make = (comp: any) =>
  connect(
    mapStateToProps,
    mapDispatchToProps,
    mergeProps,
  )(comp)

export default make
