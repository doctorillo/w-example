import { concat, Observable, of } from 'rxjs'
import { filter, map, mergeMap } from 'rxjs/operators'
import { combineEpics, ofType } from 'redux-observable'
import { dummy, PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_REMOVE } from '../../ActionType'
import { v4 as uuid } from 'uuid'
import {
  plannerBasicSessionAsk,
  plannerExcursionSessionAsk,
  plannerPropertySessionAsk,
  plannerSelectedAsk,
  PlannerSession,
} from '../../../types/planner/PlannerSession'
import {
  PayloadPlannerBirthDay,
  PayloadPlannerCreate,
  PayloadPlannerExcursionDateUpdate,
  PayloadPlannerExcursionFilterDates,
  PayloadPlannerExcursionFilterName,
  PayloadPlannerExcursionFilterPrice,
  PayloadPlannerExcursionFilterReset,
  PayloadPlannerExcursionFilterTags,
  PayloadPlannerExcursionMaxItems,
  PayloadPlannerExcursionSetExcursionDates, PayloadPlannerExcursionVariant,
  PayloadPlannerExcursionVariantAdd,
  PayloadPlannerExcursionVariantRemove,
  PayloadPlannerFetch,
  PayloadPlannerFirstName,
  PayloadPlannerGender,
  PayloadPlannerLastName,
  PayloadPlannerPassportExpired,
  PayloadPlannerPassportNumber,
  PayloadPlannerPassportSerial,
  PayloadPlannerPassportState,
  PayloadPlannerPointCreate,
  PayloadPlannerPointRemove,
  PayloadPlannerPointSelect,
  PayloadPlannerPropertyFilterAmenities,
  PayloadPlannerPropertyFilterBoardings,
  PayloadPlannerPropertyFilterFacilities,
  PayloadPlannerPropertyFilterIndications,
  PayloadPlannerPropertyFilterMedicals,
  PayloadPlannerPropertyFilterName,
  PayloadPlannerPropertyFilterPrice,
  PayloadPlannerPropertyFilterReset,
  PayloadPlannerPropertyFilterStar,
  PayloadPlannerPropertyFilterStop,
  PayloadPlannerPropertyFilterTherapies,
  PayloadPlannerPropertyMaxItems,
  PayloadPlannerPropertyPriceMode,
  PayloadPlannerPropertyVariant,
  PayloadPlannerPropertyVariantAdd,
  PayloadPlannerPropertyVariantRemove,
  PayloadPlannerRemove,
  PayloadPlannerSelect,
  PayloadPlannerUpdate,
  plannerCreate,
  plannerCreated,
  plannerCreateFormView,
  plannerExcursionAddVariant,
  plannerExcursionDateUpdate,
  plannerExcursionFilterReset,
  plannerExcursionFilterSetDates,
  plannerExcursionFilterSetName,
  plannerExcursionFilterSetPrice,
  plannerExcursionFilterSetTags,
  plannerExcursionRemoveVariant,
  plannerExcursionSelectVariant,
  plannerExcursionSetExcursionDates,
  plannerExcursionSetMaxItems,
  plannerFetch,
  plannerFetchCompleted,
  plannerPointCreate,
  plannerPointRemove,
  plannerPointSelect,
  plannerPropertyAddVariant,
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
  plannerPropertyRemoveVariant,
  plannerPropertySelectVariant,
  plannerPropertySetMaxItems,
  plannerPropertySetPriceMode,
  plannerRemove,
  plannerRemoved,
  plannerSelect,
  plannerSelected,
  plannerSetBirthDay,
  plannerSetFirstName,
  plannerSetGender,
  plannerSetLastName,
  plannerSetPassportExpired,
  plannerSetPassportNumber,
  plannerSetPassportSerial,
  plannerSetPassportState,
  plannerUpdate,
  plannerUpdated,
} from './actions'
import { makePlannerPoint, PlannerPoint } from '../../../types/planner/PlannerPoint'
import { PlannerRoom } from '../../../types/planner/PlannerRoom'
import { PlannerClient } from '../../../types/planner/PlannerClient'
import { makePropertyFilterParams, PropertyFilterParams } from '../../../types/planner/PropertyFilterParams'
import { ClientMeta, PassportMeta } from '../../../types/bookings/ClientMeta'
import { StateWrapper } from '../../index'
import { envCustomerCtxUpdated } from '../env/actions'
import { axiosGet$, axiosPost$ } from '../../axios'
import { QueryResult } from '../../../types/QueryResult'
import { formatLocalDate, formatLocalDateTime, parseLocalDate } from '../../../types/DateRange'
import { sortPlannerSessionFn } from './reducers'
import { PlannerExcursionDate } from '../../../types/planner/PlannerExcursionDates'
import { DateString } from '../../../types/basic/DateString'
import { ExcursionFilterParams, makeExcursionFilterParams } from '../../../types/planner/ExcursionFilterParams'
import { PlannerExcursionVariantDate, sortByDate } from '../../../types/planner/PlannerExcursionVariantDate'
import { Nullable } from '../../../types/Nullable'
import { CurrencyItem } from '../../../types/bookings/CurrencyItem'
import { Amount } from '../../../types/bookings/Amount'
import { PlannerExcursionItemClient } from '../../../types/planner/PlannerExcursionItemClient'
import { ActionSelect } from '../../../types/ActionSelect'

const plannerFetchEpic = (action$: Observable<any>) =>
  action$.pipe(
    filter(plannerFetch.match),
    mergeMap((action: PayloadPlannerFetch) => {
      return axiosGet$<PlannerSession>(`/api/planners/${action.payload}`).pipe(
        map((data: QueryResult<PlannerSession>) => {
          if (data.size > 0) {
            const items = data.items.sort(sortPlannerSessionFn)
            return plannerFetchCompleted(items)
          } else {
            return plannerFetchCompleted([])
          }
        }),
      )
    }),
  )

const upsertPlanner = (session: PlannerSession) => axiosPost$<PlannerSession, PlannerSession>('/api/planner', session).pipe(map(() => dummy('upsert complete')))

const createSessionEpic = (action$: Observable<any>) =>
  action$.pipe(
    filter(plannerCreate.match),
    mergeMap((action: PayloadPlannerCreate) => {
      const { solverId, customerId, customerName, point, dates, queryGroup } = action.payload
      const { clients, plannerPoint } = makePlannerPoint(point, dates, queryGroup)
      const session: PlannerSession = {
        id: uuid(),
        created: formatLocalDateTime(new Date()),
        updated: formatLocalDateTime(new Date()),
        solverId: solverId,
        customerId,
        customerName,
        identCode: Math.random().toString(36).substring(7).toUpperCase(),
        clients: clients,
        activePoint: plannerPoint.id,
        points: [plannerPoint],
        title: null,
        notes: null,
        bookingStep: 0,
        deleted: false,
      }
      return concat(of(envCustomerCtxUpdated({
        id: customerId,
        name: customerName,
        point: point,
        dates: dates,
      })), of(plannerCreated(session)), upsertPlanner(session))
    }),
  )

const updateSessionEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerUpdate.match),
    mergeMap((action: PayloadPlannerUpdate) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk || basicAsk.session.id !== action.payload.plannerId) {
        return of(dummy('updateSessionEpic: !basicAsk || !propertyAsk || basicAsk.session.id !== action.payload.plannerId'))
      }
      const sameCustomer = basicAsk.session.customerId === action.payload.customerId
      const sameGroup = propertyAsk.propertyGroup === action.payload.queryGroup
      const samePoint = basicAsk.point.point.value === action.payload.point.value
      const sameDate = basicAsk.point.dates.from === action.payload.dates.from && basicAsk.point.dates.to === action.payload.dates.to
      if (sameCustomer && sameGroup && samePoint && sameDate) {
        return of(dummy('updateSessionEpic: sameCustomer && sameGroup && samePoint && sameDate === true'))
      } else {
        const { customerId, customerName, point, dates, queryGroup } = action.payload
        const { clients, plannerPoint } = makePlannerPoint(point, dates, queryGroup)
        const points = [plannerPoint, basicAsk.point, ...basicAsk.points]
        const session: PlannerSession = ({
          ...basicAsk.session,
          updated: formatLocalDateTime(new Date()),
          clients: clients,
          activePoint: plannerPoint.id,
          points,
        })
        return concat(of(envCustomerCtxUpdated({
          id: customerId,
          name: customerName,
          point: point,
          dates: dates,
        })), of(plannerUpdated(session)), upsertPlanner(session))
      }
    }),
  )

const selectSessionEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSelect.match),
    mergeMap((action: PayloadPlannerSelect) => {
      if (!action.payload) {
        return concat(of(plannerSelected(action.payload)), of(plannerCreateFormView(false)))
      }
      const planner: PlannerSession | undefined = store$.value.sliceTripPlanner.internal.find(x => x.id === action.payload)
      if (!planner) {
        return of(dummy('selectSessionEpic: !planner'))
      }
      const activePoint = planner.activePoint
      const point = !activePoint ? planner.points[0] : planner.points.find(x => x.id === activePoint)
      if (!point) {
        return of(dummy('selectSessionEpic: !point'))
      }
      return concat(of(envCustomerCtxUpdated({
        id: planner.customerId,
        name: planner.customerName,
        point: point.point,
        dates: point.dates,
      })), of(plannerSelected(action.payload)))
    }),
  )

const removeSessionEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerRemove.match),
    mergeMap((action: PayloadPlannerRemove) => {
      const env = store$.value.sliceEnv
      const state = store$.value.sliceTripPlanner
      const planner = state.internal.find(x => x.id === action.payload)
      if (!planner) {
        return of(dummy('removeSessionEpic: !planner'))
      }
      const session = { ...planner, solverId: env.solverId, updated: formatLocalDateTime(new Date()), deleted: true }
      return concat(of(plannerRemoved(action.payload)), upsertPlanner(session))
    }),
  )

const pointCreateEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPointCreate.match),
    mergeMap((action: PayloadPlannerPointCreate) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk) {
        return of(dummy('pointCreateEpic: !basicAsk'))
      }
      const { point, dates, queryGroup } = action.payload
      const { plannerPoint, clients } = makePlannerPoint(point, dates, queryGroup)
      const session: PlannerSession = ({
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        clients: clients,
        activePoint: plannerPoint.id,
        points: [plannerPoint, basicAsk.point, ...basicAsk.points],
      })
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointSelectEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPointSelect.match),
    mergeMap((action: PayloadPlannerPointSelect) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk) {
        return of(dummy('pointSelectEpic: !basicAsk'))
      }
      const activePoint = basicAsk.points.find(x => x.point.value === action.payload)
      if (!activePoint) {
        return of(dummy('pointSelectEpic: !activePoint'))
      }
      const points = [activePoint, ...basicAsk.points.filter(x => x.id !== activePoint.id)]
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        activePoint: activePoint.id,
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointRemoveEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPointRemove.match),
    mergeMap((action: PayloadPlannerPointRemove) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk) {
        return of(dummy('pointRemoveEpic: !basicAsk'))
      }
      if (basicAsk.point.id === action.payload) {
        if (basicAsk.points.length === 0) {
          return of(dummy('pointRemoveEpic: basicAsk.points.length === 0'))
        } else {
          const points = [basicAsk.point, ...basicAsk.points].filter(x => x.id !== action.payload)
          const activePoint = points[0]?.id
          const session: PlannerSession = {
            ...basicAsk.session,
            updated: formatLocalDateTime(new Date()),
            activePoint,
            points,
          }
          return concat(of(plannerUpdated(session)), upsertPlanner(session))
        }
      } else {
        const session: PlannerSession = {
          ...basicAsk.session,
          updated: formatLocalDateTime(new Date()),
          points: basicAsk.points,
        }
        return concat(of(plannerUpdated(session)), upsertPlanner(session))
      }
    }),
  )

const pointPropertyFilterNameEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetName.match),
    mergeMap((action: PayloadPlannerPropertyFilterName) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterNameEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, name: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterStarEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetStar.match),
    mergeMap((action: PayloadPlannerPropertyFilterStar) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterStarEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, stars: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterPriceEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetPrice.match),
    mergeMap((action: PayloadPlannerPropertyFilterPrice) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterPriceEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, price: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterBoardingEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetBoardings.match),
    mergeMap((action: PayloadPlannerPropertyFilterBoardings) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterBoardingEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, boardings: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterAmenityEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetAmenities.match),
    mergeMap((action: PayloadPlannerPropertyFilterAmenities) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterAmenityEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, amenities: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterFacilityEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetFacilities.match),
    mergeMap((action: PayloadPlannerPropertyFilterFacilities) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterFacilityEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, facilities: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterMedicalEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetMedicals.match),
    mergeMap((action: PayloadPlannerPropertyFilterMedicals) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterMedicalEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, medicals: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterIndicationEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetIndications.match),
    map((action: PayloadPlannerPropertyFilterIndications) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterIndicationEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, indications: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterTherapyEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetTherapies.match),
    mergeMap((action: PayloadPlannerPropertyFilterTherapies) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterTherapyEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, therapies: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointPropertyFilterStopEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterSetStop.match),
    mergeMap((action: PayloadPlannerPropertyFilterStop) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('pointPropertyFilterStopEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = { ...propertyAsk.propertyFilter, viewStop: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const filterPropertyResetEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyFilterReset.match),
    mergeMap((_: PayloadPlannerPropertyFilterReset) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('filterPropertyResetEpic: !basicAsk || !propertyAsk'))
      }
      const filter: PropertyFilterParams = makePropertyFilterParams()
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setPropertyMaxItemsEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertySetMaxItems.match),
    mergeMap((action: PayloadPlannerPropertyMaxItems) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('setPropertyMaxItemsEpic: !basicAsk || !propertyAsk'))
      }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, maxItems: action.payload } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setViewModeEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertySetPriceMode.match),
    mergeMap((action: PayloadPlannerPropertyPriceMode) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy('setViewModeEpic: !basicAsk || !propertyAsk'))
      }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, priceView: action.payload } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const addAccommodationVariantEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyAddVariant.match),
    mergeMap((action: PayloadPlannerPropertyVariantAdd) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk || !propertyAsk.propertyRooms.find(
        x => x.id === action.payload.plannerRoomId,
      )) {
        return of(dummy('addAccommodationVariantEpic: !basicAsk || !propertyAsk ||'))
      }
      const rooms: PlannerRoom[] = propertyAsk.propertyRooms.map(x => {
        if (x.id === action.payload.plannerRoomId) {
          return { ...x, variants: [...x.variants, action.payload.room] }
        } else {
          return x
        }
      })
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          const propertyVariantCount = rooms.reduce((a, x) => a + x.variants.filter(z => !z.markAsDelete).length, 0)
          const variantCount = (x?.excursion?.variantCount || 0) + propertyVariantCount
          return { ...x, variantCount, property: { ...x.property, rooms, variantCount: propertyVariantCount } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const removeAccommodationVariantEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertyRemoveVariant.match),
    ofType(PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_REMOVE),
    mergeMap((action: PayloadPlannerPropertyVariantRemove) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk || !propertyAsk.propertyRooms.find(
        x => x.id === action.payload.plannerRoomId,
      )) {
        return of(dummy('removeAccommodationVariantEpic: !basicAsk || !propertyAsk'))
      }
      const rooms: PlannerRoom[] = propertyAsk.propertyRooms.map(x => {
        if (x.id === action.payload.plannerRoomId) {
          const variants = x.variants.map(z => {
            if (z.id !== action.payload.id) {
              return z
            } else {
              return { ...z, markAsDelete: true }
            }
          })
          return { ...x, variants }
        } else {
          return x
        }
      })
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          const propertyVariantCount = rooms.reduce((a, x) => a + x.variants.filter(z => !z.markAsDelete).length, 0)
          const variantCount = (x?.excursion?.variantCount || 0) + propertyVariantCount
          return { ...x, variantCount, property: { ...x.property, rooms, variantCount: propertyVariantCount } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const selectAccommodationVariantEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerPropertySelectVariant.match),
    mergeMap((action: PayloadPlannerPropertyVariant) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk || !propertyAsk.propertyRooms.find(
        x => x.id === action.payload.plannerRoomId,
      )) {
        return of(dummy('selectAccommodationVariantEpic: !basicAsk || !propertyAsk'))
      }
      const rooms: PlannerRoom[] = propertyAsk.propertyRooms.map(x => {
        if (x.id === action.payload.plannerRoomId) {
          return { ...x, selected: action.payload.itemId }
        } else {
          return x
        }
      })
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.property) {
          return x
        } else {
          return { ...x, property: { ...x.property, rooms } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

// EXCURSIONS

const pointExcursionFilterNameEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionFilterSetName.match),
    mergeMap((action: PayloadPlannerExcursionFilterName) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('pointExcursionFilterNameEpic: !basicAsk || !excursionAsk'))
      }
      const filter = { ...excursionAsk.excursionFilter, name: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          return { ...x, excursion: { ...x.excursion, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointExcursionFilterPriceEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionFilterSetPrice.match),
    mergeMap((action: PayloadPlannerExcursionFilterPrice) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('pointExcursionFilterPriceEpic: !basicAsk || !excursionAsk'))
      }
      const filter = { ...excursionAsk.excursionFilter, price: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          return { ...x, excursion: { ...x.excursion, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointExcursionFilterDatesEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionFilterSetDates.match),
    mergeMap((action: PayloadPlannerExcursionFilterDates) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('pointExcursionFilterDatesEpic: !basicAsk || !excursionAsk'))
      }
      const filter = { ...excursionAsk.excursionFilter, viewDates: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          return { ...x, excursion: { ...x.excursion, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const pointExcursionFilterTagsEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionFilterSetTags.match),
    mergeMap((action: PayloadPlannerExcursionFilterTags) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('pointExcursionFilterTagsEpic: !basicAsk || !excursionAsk'))
      }
      const filter = { ...excursionAsk.excursionFilter, tags: action.payload }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          return { ...x, excursion: { ...x.excursion, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const filterExcursionResetEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionFilterReset.match),
    mergeMap((_: PayloadPlannerExcursionFilterReset) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('filterExcursionResetEpic: !basicAsk || !propertyAsk'))
      }
      const filter: ExcursionFilterParams = makeExcursionFilterParams()
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          return { ...x, excursion: { ...x.excursion, filter } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setExcursionMaxItemsEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionSetMaxItems.match),
    mergeMap((action: PayloadPlannerExcursionMaxItems) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('setExcursionMaxItemsEpic: !basicAsk || !excursionAsk'))
      }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          return { ...x, excursion: { ...x.excursion, maxItems: action.payload } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setExcursionSetDatesEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionSetExcursionDates.match),
    mergeMap((action: PayloadPlannerExcursionSetExcursionDates) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('setExcursionSetDatesEpic: !basicAsk || !excursionAsk'))
      }
      const points: PlannerPoint[] = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          const dates = action.payload.reduce((a, x) => {
            if (a.includes(x.selected)) {
              return a
            } else {
              return [...a, x.selected]
            }
          }, [] as DateString[]).map(parseLocalDate)
            .sort((l, r) => l.valueOf() - r.valueOf())
            .map(formatLocalDate)
          return { ...x, excursion: { ...x.excursion, dates, excursionDates: action.payload } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setExcursionDateUpdateEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionDateUpdate.match),
    mergeMap((action: PayloadPlannerExcursionDateUpdate) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('setExcursionDateUpdateEpic: !basicAsk || !excursionAsk'))
      }
      const { excursionOfferId, selected } = action.payload
      const excursionDates: PlannerExcursionDate[] = excursionAsk.excursionSelectedDates.map(x => {
        if (x.excursionOfferId !== excursionOfferId) {
          return x
        } else {
          return { excursionOfferId, selected }
        }
      })
      return concat(of(plannerExcursionSetExcursionDates(excursionDates)))
    }),
  )

const addExcursionVariantEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionAddVariant.match),
    mergeMap((action: PayloadPlannerExcursionVariantAdd) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('addExcursionVariantEpic: !basicAsk || !excursionAsk'))
      }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          const { clientId, item } = action.payload
          const excursionItems = x.excursion.items
          let addItem: Nullable<PlannerExcursionVariantDate> = null
          const existedItem = excursionItems.find(z => z.date === item.date) || null
          if (!existedItem) {
            addItem = {
              date: item.date,
              variants: [item],
              variantCount: 1,
            }
          } else {
            const existedVariant = existedItem.variants.find(z => z.excursionId === item.excursionId && !!z.clients.find(y => clientId.includes(y.clientId)))
            if (!existedVariant) {
              addItem = { ...existedItem, variants: [...existedItem.variants, item], variantCount: existedItem.variantCount + 1}
            }
          }
          const items = !addItem ? excursionItems : [...excursionItems.filter(z => z.date !== item.date), addItem]
          const excursionVariantCount = items.reduce((a, x) => a + x.variants.length, 0)
          const variantCount = (x?.property?.variantCount || 0) + excursionVariantCount
          return { ...x, variantCount, excursion: { ...x.excursion, variantCount: excursionVariantCount, items: items.sort(sortByDate) } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const removeExcursionVariantEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionRemoveVariant.match),
    mergeMap((action: PayloadPlannerExcursionVariantRemove) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('removeExcursionVariantEpic: !basicAsk || !excursionAsk'))
      }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          const { plannerClientId, itemId} = action.payload
          const items = x.excursion.items.map(z => {
            if (!z.variants.find(w => w.id === itemId)) {
              return z
            } else {
              const variants = z.variants.map(y => {
                if (y.id !== itemId || !y.clients.find(w => plannerClientId.includes(w.clientId))) {
                  return y
                } else {
                  const clients: PlannerExcursionItemClient[] = y.clients.reduce((a, y) => {
                    if (plannerClientId.includes(y.clientId)){
                      return a
                    } else {
                      return [...a, y]
                    }
                  }, [] as PlannerExcursionItemClient[])
                  const total = clients.reduce((a, w) => ({...a, value: a.value + w.price.value}), { value: 0, currency: CurrencyItem.Euro } as Amount)
                  return { ...y, clients, total }
                }
              }).filter(y => y.clients.length > 0)
              const variantCount = variants.length
              return { ...z, variants: variants, variantCount }
            }
          }).filter(z => z.variantCount > 0)
          const selected = x.excursion.selected.filter(z => z !== itemId)
          const excursionVariantCount = items.reduce((a, x) => a + x.variants.length, 0)
          const variantCount = (x?.property?.variantCount || 0) + excursionVariantCount
          return { ...x, variantCount, excursion: { ...x.excursion, items, selected, variantCount: excursionVariantCount } }
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const selectExcursionVariantEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerExcursionSelectVariant.match),
    mergeMap((action: PayloadPlannerExcursionVariant) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const excursionAsk = plannerExcursionSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !excursionAsk) {
        return of(dummy('selectExcursionVariantEpic: !basicAsk || !excursionAsk'))
      }
      const points = [basicAsk.point, ...basicAsk.points].map(x => {
        if (x.id !== basicAsk.point.id || !x.excursion) {
          return x
        } else {
          const {actionType, itemId} = action.payload
          let selected = []
          if (actionType === ActionSelect.Select){
            selected = [...x.excursion.selected, itemId]
          } else {
            selected = x.excursion.selected.filter(z => z !== itemId)
          }
          const excursion = {...x.excursion, selected: selected}
          return {...x, excursion}
        }
      })
      const session: PlannerSession = {
        ...basicAsk.session,
        updated: formatLocalDateTime(new Date()),
        points,
      }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

// CLIENTS

const setFirstNameEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSetFirstName.match),
    mergeMap((action: PayloadPlannerFirstName) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !basicAsk.clients.find(
        (x: PlannerClient) => x.id === action.payload.id,
      )) {
        return of(dummy('setFirstNameEpic: !basicAsk'))
      }
      const firstName = action.payload.value
      const plannerSession: PlannerSession = basicAsk.session
      const clients: PlannerClient[] = plannerSession.clients.map(
        (x: PlannerClient) => {
          if (x.id === action.payload.id) {
            const meta: ClientMeta = { ...x.meta, firstName }
            return { ...x, meta }
          } else {
            return x
          }
        },
      )
      const session = { ...plannerSession, updated: formatLocalDateTime(new Date()), clients }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setLastNameEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSetLastName.match),
    mergeMap((action: PayloadPlannerLastName) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !basicAsk.clients.find(
        (x: PlannerClient) => x.id === action.payload.id,
      )
      ) {
        return of(dummy('setLastNameEpic: !basicAsk'))
      }
      const lastName = action.payload.value
      const plannerSession: PlannerSession = basicAsk.session
      const clients: PlannerClient[] = plannerSession.clients.map(
        (x: PlannerClient) => {
          if (x.id === action.payload.id) {
            const meta: ClientMeta = { ...x.meta, lastName }
            return { ...x, meta }
          } else {
            return x
          }
        },
      )
      const session = { ...plannerSession, updated: formatLocalDateTime(new Date()), clients }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setGenderEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSetGender.match),
    mergeMap((action: PayloadPlannerGender) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !basicAsk.clients.find(
        (x: PlannerClient) => x.id === action.payload.id,
      )) {
        return of(dummy('setGenderEpic: !basicAsk'))
      }
      const gender = action.payload.value
      const plannerSession: PlannerSession = basicAsk.session
      const clients: PlannerClient[] = plannerSession.clients.map(
        (x: PlannerClient) => {
          if (x.id === action.payload.id) {
            const meta: ClientMeta = { ...x.meta, gender }
            return { ...x, meta }
          } else {
            return x
          }
        },
      )
      const session = { ...plannerSession, updated: formatLocalDateTime(new Date()), clients }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setBirthDayEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSetBirthDay.match),
    mergeMap((action: PayloadPlannerBirthDay) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !basicAsk.clients.find(
        (x: PlannerClient) => x.id === action.payload.id,
      )
      ) {
        return of(dummy('setBirthDayEpic: !basicAsk'))
      }
      const birthDay = action.payload.value
      const plannerSession: PlannerSession = basicAsk.session
      const clients: PlannerClient[] = plannerSession.clients.map(
        (x: PlannerClient) => {
          if (x.id === action.payload.id) {
            const meta: ClientMeta = { ...x.meta, birthDay }
            return { ...x, meta }
          } else {
            return x
          }
        },
      )
      const session = { ...plannerSession, updated: formatLocalDateTime(new Date()), clients }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setPassportSerialEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSetPassportSerial.match),
    mergeMap((action: PayloadPlannerPassportSerial) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !basicAsk.clients.find(
        (x: PlannerClient) => x.id === action.payload.id,
      )
      ) {
        return of(dummy('setPassportSerialEpic: !basicAsk'))
      }
      const plannerSession: PlannerSession = basicAsk.session
      const clients: PlannerClient[] = plannerSession.clients.map(
        (x: PlannerClient) => {
          if (x.id === action.payload.id) {
            const passport: PassportMeta = { ...x.meta.passport, serial: action.payload.value }
            const meta: ClientMeta = { ...x.meta, passport }
            return { ...x, meta }
          } else {
            return x
          }
        },
      )
      const session = { ...plannerSession, updated: formatLocalDateTime(new Date()), clients }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setPassportNumberEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSetPassportNumber.match),
    mergeMap((action: PayloadPlannerPassportNumber) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !basicAsk.clients.find(
        (x: PlannerClient) => x.id === action.payload.id,
      )
      ) {
        return of(dummy('setPassportNumberEpic: !basicAsk'))
      }
      const plannerSession: PlannerSession = basicAsk.session
      const clients: PlannerClient[] = plannerSession.clients.map(
        (x: PlannerClient) => {
          if (x.id === action.payload.id) {
            const passport: PassportMeta = { ...x.meta.passport, number: action.payload.value }
            const meta: ClientMeta = { ...x.meta, passport }
            return { ...x, meta }
          } else {
            return x
          }
        },
      )
      const session = { ...plannerSession, updated: formatLocalDateTime(new Date()), clients }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setPassportExpiredEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSetPassportExpired.match),
    mergeMap((action: PayloadPlannerPassportExpired) => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !basicAsk.clients.find(
        (x: PlannerClient) => x.id === action.payload.id,
      )) {
        return of(dummy('setPassportExpiredEpic: !basicAsk'))
      }
      const plannerSession: PlannerSession = basicAsk.session
      const clients: PlannerClient[] = plannerSession.clients.map(
        (x: PlannerClient) => {
          if (x.id === action.payload.id) {
            const passport: PassportMeta = { ...x.meta.passport, expiredAt: action.payload.value }
            const meta: ClientMeta = { ...x.meta, passport }
            return { ...x, meta }
          } else {
            return x
          }
        },
      )
      const session = { ...plannerSession, updated: formatLocalDateTime(new Date()), clients }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

const setPassportStateEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(plannerSetPassportState.match),
    mergeMap((action: PayloadPlannerPassportState) => {
      const ask = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!ask || !ask.clients.find(
        (x: PlannerClient) => x.id === action.payload.id,
      )
      ) {
        return of(dummy('setPassportStateEpic: !basicAsk'))
      }
      const plannerSession: PlannerSession = ask.session
      const clients: PlannerClient[] = plannerSession.clients.map(
        (x: PlannerClient) => {
          if (x.id === action.payload.id) {
            const passport: PassportMeta = { ...x.meta.passport, state: action.payload.value }
            const meta: ClientMeta = { ...x.meta, passport }
            return { ...x, meta }
          } else {
            return x
          }
        },
      )
      const session = { ...plannerSession, updated: formatLocalDateTime(new Date()), clients }
      return concat(of(plannerUpdated(session)), upsertPlanner(session))
    }),
  )

export default combineEpics(
  plannerFetchEpic,
  createSessionEpic,
  updateSessionEpic,
  selectSessionEpic,
  removeSessionEpic,
  pointCreateEpic,
  pointSelectEpic,
  pointRemoveEpic,
  pointPropertyFilterNameEpic,
  pointPropertyFilterStarEpic,
  pointPropertyFilterPriceEpic,
  pointPropertyFilterBoardingEpic,
  pointPropertyFilterAmenityEpic,
  pointPropertyFilterFacilityEpic,
  pointPropertyFilterMedicalEpic,
  pointPropertyFilterIndicationEpic,
  pointPropertyFilterTherapyEpic,
  pointPropertyFilterStopEpic,
  filterPropertyResetEpic,
  setPropertyMaxItemsEpic,
  setViewModeEpic,
  addAccommodationVariantEpic,
  removeAccommodationVariantEpic,
  selectAccommodationVariantEpic,
  pointExcursionFilterNameEpic,
  pointExcursionFilterPriceEpic,
  pointExcursionFilterDatesEpic,
  pointExcursionFilterTagsEpic,
  filterExcursionResetEpic,
  setExcursionMaxItemsEpic,
  setExcursionDateUpdateEpic,
  setExcursionSetDatesEpic,
  addExcursionVariantEpic,
  removeExcursionVariantEpic,
  selectExcursionVariantEpic,
  setFirstNameEpic,
  setLastNameEpic,
  setGenderEpic,
  setBirthDayEpic,
  setPassportSerialEpic,
  setPassportNumberEpic,
  setPassportExpiredEpic,
  setPassportStateEpic,
)
