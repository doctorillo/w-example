import { axiosGet$ } from '../../../axios'
import { combineEpics } from 'redux-observable'
import { filter, map, mergeMap } from 'rxjs/operators'
import { indicationFetch, indicationFilled } from './actions'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { STATE_ENV } from '../../env/reducers'
import { Observable } from 'rxjs'
import { StateWrapper } from '../../../index'

const indicationFetchEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(indicationFetch.match),
    mergeMap(() => {
      const state: STATE_ENV = store$.value.sliceEnv
      return axiosGet$<EnumUI>(
        `/api/treatment-indications/${state.lang}`).pipe(
          map((data) => indicationFilled(data.items))
      )
    })
  )

export default combineEpics(indicationFetchEpic)
