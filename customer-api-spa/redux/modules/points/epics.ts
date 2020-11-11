import { filter, map, mergeMap } from 'rxjs/operators'
import { combineEpics } from 'redux-observable'

import { PayloadPointFetch, pointFetch, pointFilled } from './actions'
import { axiosGet$ } from '../../axios'
import { PointUI } from '../../../types/geo/PointUI'
import { EnvState } from '../../../types/contexts/EnvState'
import { Observable } from 'rxjs'
import { StateWrapper } from '../../index'

export const fetchPointsEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(pointFetch.match),
    mergeMap((_: PayloadPointFetch) => {
      const store: EnvState = store$.value.sliceEnv
      return axiosGet$<PointUI>(`/api/ctx/points/0/${store.lang}`).pipe(map((data, _) => pointFilled(data.items)))
    }),
  )

export default combineEpics(fetchPointsEpic)

