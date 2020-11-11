import { axiosGet$ } from '../../../axios'
import { combineEpics } from 'redux-observable'
import { filter, map, mergeMap } from 'rxjs/operators'
import { amenityFetch, amenityFilled } from './actions'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { STATE_ENV } from '../../env/reducers'
import { Observable } from 'rxjs'
import { StateWrapper } from '../../../index'

const amenityFetchEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(amenityFetch.match),
    mergeMap(() => {
      const state: STATE_ENV = store$.value.sliceEnv
      return axiosGet$<EnumUI>(
        `/api/amenities/${state.lang}`).pipe(
          map((data) => amenityFilled(data.items))
      )
    })
  )

export default combineEpics(
  amenityFetchEpic
)
