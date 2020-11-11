import { axiosGet$ } from '../../../axios'
import { combineEpics } from 'redux-observable'
import { filter, map, mergeMap } from 'rxjs/operators'
import { facilityFetch, facilityFilled } from './actions'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { STATE_ENV } from '../../env/reducers'
import { Observable } from 'rxjs'
import { StateWrapper } from '../../../index'

const facilityFetchEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(facilityFetch.match),
    mergeMap(() => {
      const state: STATE_ENV = store$.value.sliceEnv
      return axiosGet$<EnumUI>(
        `/api/room-facilities/${state.lang}`).pipe(
        map(data => {
          if (data.hasError || data.size === 0) {
            return facilityFilled([])
          }
          return facilityFilled(data.items || [])
        }))
    }))


export default combineEpics(
  facilityFetchEpic,
)
