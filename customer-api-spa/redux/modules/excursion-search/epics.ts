import { concat, Observable, of } from 'rxjs'
import { filter, mergeMap } from 'rxjs/operators'
import { combineEpics } from 'redux-observable'

import { excursionSearchClean, excursionSearchFetch, excursionSearchFilled } from './actions'
import { dummy } from '../../ActionType'
import { axiosPost$ } from '../../axios'
import { LangItem } from '../../../types/LangItem'
import { StateWrapper } from '../../index'
import { plannerBasicSessionAsk, plannerSelectedAsk } from '../../../types/planner/PlannerSession'
import { FetchExcursionCardQ } from '../../../types/excursion/cmd/FetchExcursionCardQ'
import { CurrencyItem } from '../../../types/bookings/CurrencyItem'
import { ExcursionCardUI } from '../../../types/bookings/ExcursionCardUI'
import { plannerExcursionSetExcursionDates } from '../trip-planner/actions'
import { formatLocalDate, parseLocalDate } from '../../../types/DateRange'
import { PlannerExcursionDate } from '../../../types/planner/PlannerExcursionDates'
import { excursionTagFetch } from '../excursion-tag/actions'

const fetchExcursionsEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(excursionSearchFetch.match),
    mergeMap(() => {
      const basicAsk = plannerBasicSessionAsk(plannerSelectedAsk(store$.value.sliceTripPlanner))
      if (!basicAsk) {
        return of(dummy('fetchExcursionsEpic: !basicAsk'))
      }
      const query: FetchExcursionCardQ = {
        customerId: basicAsk.session.customerId,
        lang: LangItem.Ru,
        pointId: basicAsk.point.point.value,
        pointCategory: basicAsk.point.point.category,
        currency: CurrencyItem.Euro,
        dates: basicAsk.point.dates,
      }
      const ajax = axiosPost$<FetchExcursionCardQ, ExcursionCardUI>(`/api/excursions/search`, query).pipe(
        mergeMap(data => {
          const excursionDates: PlannerExcursionDate[] = data.items.reduce((a: PlannerExcursionDate[], x: ExcursionCardUI) => {
            const dates = x.dates.map(parseLocalDate)
              .sort((l, r) => l.valueOf() - r.valueOf())
              .map(formatLocalDate)
            return [...a, {
              excursionOfferId: x.id,
              selected: dates[0],
            }]
          }, [] as PlannerExcursionDate[])
          return concat(of(plannerExcursionSetExcursionDates(excursionDates)), of(excursionSearchFilled(data.items)))
        }),
      )
      return concat(of(excursionSearchClean()), of(excursionTagFetch()), ajax)
    }),
  )


export default combineEpics(fetchExcursionsEpic)