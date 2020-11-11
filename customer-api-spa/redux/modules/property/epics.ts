import { filter, map, mergeMap } from 'rxjs/operators'
import { combineEpics } from 'redux-observable'

import { propertyDescriptionFetch, propertyDescriptionFilled, propertyPriceFetch, propertyPriceFilled } from './actions'
import { dummy } from '../../ActionType'
import { axiosGet$, axiosPost$ } from '../../axios'
import { PriceUnitUI } from '../../../types/property/prices/PriceUnitUI'
import { STATE_PROPERTY } from './reducers'
import { PropertyDescriptionUI } from '../../../types/property/PropertyDescriptionUI'
import { Observable, of } from 'rxjs'
import { StateWrapper } from '../../index'
import { envAsk } from '../../../types/contexts/EnvState'
import {
  plannerBasicSessionAsk,
  plannerPropertySessionAsk,
  plannerSelectedAsk,
} from '../../../types/planner/PlannerSession'
import { QueryGroupQ } from '../../../types/bookings/QueryGroup'

export const fetchPropertyDescriptionEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(propertyDescriptionFetch.match),
    mergeMap((_) => {
      const state: STATE_PROPERTY = store$.value.sliceProperty
      return axiosGet$<PropertyDescriptionUI>(`/api/property/descriptions/${state.id}/0`).pipe(map((data, _) => {
          if (data.size === 0){
            return propertyDescriptionFilled(null)
          }
          return propertyDescriptionFilled(data.items[0])
        })
      )
    }),
  )

export const fetchPropertyPriceVariantsEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(propertyPriceFetch.match),
    mergeMap((_) => {
      const askEnv = envAsk(store$.value.sliceEnv)
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!askEnv || !basicAsk || !propertyAsk){
        return of(dummy())
      }
      const group: QueryGroupQ = {
        category: propertyAsk.propertyGroup.category,
        rooms: propertyAsk.propertyGroup.rooms
      }
      const state: STATE_PROPERTY = store$.value.sliceProperty
      const query = {
        lang: 0,
        customerId: basicAsk.session.customerId,
        propertyId: state.id,
        dates: basicAsk.point.dates,
        group: group
      }
      return axiosPost$<typeof query, PriceUnitUI>(`/api/property/price-variants`,
        query).pipe(map((data, _) => {
        if (data.hasError || data.size === 0) {
          return propertyPriceFilled([])
        }
        return propertyPriceFilled(data.items)
      }))
    }),
  )

export default combineEpics(fetchPropertyDescriptionEpic, fetchPropertyPriceVariantsEpic)

