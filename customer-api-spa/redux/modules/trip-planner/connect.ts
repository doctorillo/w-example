import { connect } from 'react-redux'
import { createSelector } from 'reselect'
import { STATE_ENV } from '../env/reducers'
import { ResultKind } from '../../../types/ResultKind'
import {
  plannerCreate,
  plannerCreateFormView,
  plannerExcursionAddVariant,
  plannerExcursionRemoveVariant, plannerExcursionSelectVariant,
  plannerFetch,
  plannerPropertyRemoveVariant,
  plannerPropertySelectVariant,
  plannerRemove,
  plannerSelect,
  plannerSetBirthDay, plannerSetBookingStep,
  plannerSetFirstName,
  plannerSetGender, plannerSetLastName,
  plannerSetPassportExpired,
  plannerSetPassportNumber,
  plannerSetPassportSerial,
  plannerSetPassportState,
  plannerToggle,
  plannerUpdate,
} from './actions'
import { STATE_TRIP_PLANNER } from './reducers'
import {
  plannerBasicSessionAsk,
  plannerExcursionSessionAsk,
  plannerPropertySessionAsk,
  plannerSelectedAsk,
} from '../../../types/planner/PlannerSession'
import { pointFetch } from '../points/actions'
import { GenderItem } from '../../../types/parties/GenderItem'
import { Nullable } from '../../../types/Nullable'
import { RootState } from '../../index'
import { propertySearchFetch } from '../property-search/actions'
import { Dispatch } from 'redux'
import { AppProps } from '../env/connect'
import { EitherEnv, fold, makeLeft, makeRight } from '../../../types/EitherEnv'
import { PlannerSessionCreate } from '../../../types/planner/cmd/PlannerSessionCreate'
import { propertySet } from '../property/actions'
import { Uuid } from '../../../types/basic/Uuid'
import { PlannerSessionUpdate } from '../../../types/planner/cmd/PlannerSessionUpdate'
import { EnvAsk, envAsk } from '../../../types/contexts/EnvState'
import {
  BasicPlanEnv,
  ExcursionPlanEnv,
  PropertyPlanEnv,
  TripPlannerClientFn,
  TripPlannerEnv,
  TripPlannerExcursionVariantFn,
  TripPlannerFn,
  TripPlannerPageProps,
  TripPlannerPropertyVariantFn,
} from './TripPlannerPageProps'
import { LangItem } from '../../../types/LangItem'
import { excursionSearchFetch } from '../excursion-search/actions'
import { PlannerExcursionItem } from '../../../types/planner/PlannerExcursionItem'
import { ActionSelect } from '../../../types/ActionSelect'


const envValue = (state: RootState): STATE_ENV => state.sliceEnv
const plannerValue = (state: RootState): STATE_TRIP_PLANNER => state.sliceTripPlanner

const getProps = () =>
  createSelector(
    envValue,
    plannerValue,
    (
      env,
      plannerState,
    ): EitherEnv<ResultKind, TripPlannerEnv> => {
      if (plannerState.status !== ResultKind.Completed) {
        return makeLeft(plannerState.status)
      }
      const others = plannerState.extraEnv?.selected ? plannerState.data.filter(x => x.id !== plannerState.extraEnv.selected) : plannerState.data
      const plannerEnv: TripPlannerEnv = {
        lang: env.lang,
        basic: null,
        property: null,
        excursion: null,
        others: others,
        variantCount: 0,
        createFormView: plannerState.extraEnv?.create || false,
        preview: plannerState.extraEnv?.preview || false,
        status: plannerState.status,
      }
      let basicEnv: Nullable<BasicPlanEnv> = null
      if (plannerState.extraEnv.selected) {
        const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(plannerState))
        const askEnv: EnvAsk | null = envAsk(env)
        basicEnv = basicAsk && askEnv && ({
          customerId: askEnv.customerId,
          customerName: askEnv.customerName,
          identCode: basicAsk.session.identCode,
          plannerId: basicAsk.session.id,
          plannerClients: basicAsk.clients,
          plannerPoint: basicAsk.point,
          plannerPoints: basicAsk.points,
          bookingStep: basicAsk.bookingStep,
        })
      }
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(plannerState))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(plannerState))
      if (!propertyAsk && !excursionAsk) {
        return makeRight(plannerEnv)
      }
      let propertyPlan: Nullable<PropertyPlanEnv> = null
      if (propertyAsk) {
        const { propertyId, propertyGroup, propertyRooms, propertyVariantCount, propertyFilter, propertyMaxItems, propertyPriceView } = propertyAsk
        propertyPlan = {
          plannerPropertyId: propertyId,
          propertyQueryGroup: propertyGroup,
          propertyRooms: propertyRooms,
          propertyVariantCount: propertyVariantCount,
          propertyFilter: propertyFilter,
          propertyPriceView: propertyPriceView,
          propertyMaxItems: propertyMaxItems,
        }
      }
      let excursionPlan: Nullable<ExcursionPlanEnv> = null
      if (excursionAsk) {
        const { excursionId, excursionGroup, excursionClientItems, excursionSelected, excursionFilter, excursionMaxItems, excursionVariantCount } = excursionAsk
        excursionPlan = {
          plannerExcursionId: excursionId,
          excursionQueryGroup: excursionGroup,
          excursionItems: excursionClientItems,
          excursionSelected: excursionSelected,
          excursionVariantCount: excursionVariantCount,
          excursionFilter: excursionFilter,
          excursionMaxItems: excursionMaxItems,
        }
      }
      return makeRight({
        ...plannerEnv,
        variantCount: (propertyPlan?.propertyVariantCount || 0) + (excursionPlan?.excursionVariantCount || 0),
        basic: basicEnv,
        property: propertyPlan,
        excursion: excursionPlan,
      })
    },
  )

const mapStateToProps = (state: RootState): EitherEnv<ResultKind, TripPlannerEnv> => getProps()(state)

const mapDispatchToProps = (dispatch: Dispatch): TripPlannerFn & TripPlannerPropertyVariantFn & TripPlannerExcursionVariantFn & TripPlannerClientFn => {
  return {
    createForm(view: boolean): void {
      dispatch(plannerCreateFormView(view))
    },
    fetch(solverId: string): void {
      dispatch(plannerFetch(solverId))
    },
    fetchPoints(): void {
      dispatch(pointFetch())
    },
    removePlan(id: string): void {
      dispatch(plannerRemove(id))
    },
    selectPlan(id: Nullable<Uuid>): void {
      dispatch(plannerSelect(id))
      id && dispatch(plannerCreateFormView(true))
    },
    togglePreview(value: boolean): void {
      dispatch(plannerToggle(value))
    },
    removeRoomVariant(
      plannerId: string,
      plannerPointId: string,
      plannerRoomId: string,
      id: string): void {
      dispatch(plannerPropertyRemoveVariant({
        plannerId,
        plannerPointId,
        plannerRoomId,
        id,
      }))
    },
    selectRoomVariant(plannerId: string, plannerPointId: string, plannerRoomId: string, itemId: string): void {
      dispatch(plannerPropertySelectVariant({
        plannerId,
        plannerPointId,
        plannerRoomId,
        itemId,
      }))
    },
    plannerCreate(cmd: PlannerSessionCreate): void {
      dispatch(plannerCreate(cmd))
      dispatch(propertySearchFetch())
      dispatch(excursionSearchFetch())
    },
    plannerUpdate(cmd: PlannerSessionUpdate): void {
      dispatch(plannerUpdate(cmd))
      dispatch(propertySearchFetch())
      dispatch(excursionSearchFetch())
    },
    plannerSetBookingStep(step: number): void {
      dispatch(plannerSetBookingStep(step))
    },
    toProperty(propertyId: string, supplierId: string, customerId: string): void {
      dispatch(propertySet({
        propertyId,
        supplierId,
        customerId,
      }))
      dispatch(propertySearchFetch())
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
        item,
      }))
    },
    selectExcursionVariant(
      plannerId: Uuid,
      plannerPointId: Uuid,
      itemId: Uuid,
    ): void {
      dispatch(plannerExcursionSelectVariant({
        plannerId,
        plannerPointId,
        itemId,
        actionType: ActionSelect.Select,
      }))
    },
    unselectExcursionVariant(
      plannerId: Uuid,
      plannerPointId: Uuid,
      itemId: Uuid,
    ): void {
      dispatch(plannerExcursionSelectVariant({
        plannerId,
        plannerPointId,
        itemId,
        actionType: ActionSelect.Unselect,
      }))
    },
    removeExcursionVariant(
      plannerId: Uuid,
      plannerPointId: Uuid,
      plannerClientId: Uuid[],
      itemId: Uuid,
    ): void {
      dispatch(plannerExcursionRemoveVariant({
        plannerId,
        plannerPointId,
        plannerClientId,
        itemId,
      }))
    },
    setFirstName(id: string, value: Nullable<string>): void {
      dispatch(plannerSetFirstName({ id, value }))
    },
    setLastName(id: string, value: Nullable<string>): void {
      dispatch(plannerSetLastName({ id, value }))
    },
    setGender(id: string, value: Nullable<GenderItem>): void {
      dispatch(plannerSetGender({ id, value }))
    },
    setBirthDay(id: string, value: Nullable<string>): void {
      dispatch(plannerSetBirthDay({ id, value }))
    },
    setPassportSerial(id: string, value: Nullable<string>): void {
      dispatch(plannerSetPassportSerial({ id, value }))
    },
    setPassportNumber(id: string, value: Nullable<string>): void {
      dispatch(plannerSetPassportNumber({ id, value }))
    },
    setPassportExpired(id: string, value: Nullable<string>): void {
      dispatch(plannerSetPassportExpired({ id, value }))
    },
    setPassportState(id: string, value: Nullable<string>): void {
      dispatch(plannerSetPassportState({ id, value }),
      )
    },
  }
}

const mergeProps = (
  stateProps: EitherEnv<ResultKind, TripPlannerEnv>,
  dispatchProps: TripPlannerFn & TripPlannerPropertyVariantFn & TripPlannerExcursionVariantFn & TripPlannerClientFn,
  ownProps: AppProps,
): TripPlannerPageProps => fold(stateProps, l => ({
  appProps: ownProps,
  env: {
    lang: LangItem.En,
    basic: null,
    property: null,
    excursion: null,
    others: [],
    variantCount: 0,
    createFormView: l === ResultKind.Completed,
    preview: false,
    status: l,
  },
  fn: dispatchProps,
  status: l,
}), r => ({
  appProps: ownProps,
  env: r,
  fn: dispatchProps,
  status: r.status,
}))

const make = (comp: any) =>
  connect(
    mapStateToProps,
    mapDispatchToProps,
    mergeProps,
  )(comp)

export default make
