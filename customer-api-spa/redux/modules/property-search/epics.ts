import { concat, Observable, of } from 'rxjs'
import { filter, mergeMap } from 'rxjs/operators'
import { combineEpics } from 'redux-observable'

import { propertySearchClean, propertySearchFetch, propertySearchFilled } from './actions'
import { dummy } from '../../ActionType'
import { axiosPost$ } from '../../axios'
import { LangItem } from '../../../types/LangItem'
import { StateWrapper } from '../../index'
import { QueryGroupQ } from '../../../types/bookings/QueryGroup'
import { FetchPropertyCardQ } from '../../../types/property/cmd/FetchPropertyCardQ'
import { PropertyCardUI } from '../../../types/bookings/PropertyCardUI'
import {
  plannerBasicSessionAsk,
  plannerPropertySessionAsk,
  plannerSelectedAsk,
} from '../../../types/planner/PlannerSession'
import { amenityFetch } from '../property-extras/amenities/actions'
import { facilityFetch } from '../property-extras/facilities/actions'
import { therapyFetch } from '../property-extras/therapies/actions'
import { indicationFetch } from '../property-extras/indications/actions'
import { plannerPropertyFilterSetPrice } from '../trip-planner/actions'

export const fetchPropertiesEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(propertySearchFetch.match),
    mergeMap(() => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      const propertyAsk = plannerPropertySessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk || !propertyAsk) {
        return of(dummy())
      }
      const group: QueryGroupQ = {
        category: propertyAsk.propertyGroup.category,
        rooms: propertyAsk.propertyGroup.rooms,
      }
      const query: FetchPropertyCardQ = {
        customerId: basicAsk.session.customerId,
        lang: LangItem.Ru,
        pointId: basicAsk.point.point.value,
        pointCategory: basicAsk.point.point.category,
        dates: basicAsk.point.dates,
        group: group,
      }
      const ajax = axiosPost$<FetchPropertyCardQ, PropertyCardUI>(`/api/properties/search`, query).pipe(
        mergeMap(data => {
          const minPrice = data.items.reduce((acc: number, x: PropertyCardUI) => {
            const { bestPrice: { total : { value } } } = x
            if (acc > value){
              return value
            } else {
              return acc
            }
          }, 0)
          const maxPrice = data.items.reduce((acc: number, x: PropertyCardUI) => {
            const { bestPrice: { total : { value } } } = x
            if (acc < value){
              return value
            } else {
              return acc
            }
          }, 0)
          return concat(of(plannerPropertyFilterSetPrice([minPrice, maxPrice])), of(propertySearchFilled(data.items)))
        }),
      )
      return concat(of(propertySearchClean()), of(amenityFetch()), of(facilityFetch()), of(therapyFetch()), of(indicationFetch()), ajax)
    }),
  )

export default combineEpics(fetchPropertiesEpic)