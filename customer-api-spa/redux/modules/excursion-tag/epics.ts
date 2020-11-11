import { axiosGet$ } from '../../axios'
import { combineEpics } from 'redux-observable'
import { filter, map, mergeMap } from 'rxjs/operators'
import { excursionTagFetch, excursionTagFilled } from './actions'
import { EnumUI } from '../../../types/basic/EnumUI'
import { STATE_ENV } from '../env/reducers'
import { Observable } from 'rxjs'
import { StateWrapper } from '../../index'

const excursionFetchEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(excursionTagFetch.match),
    mergeMap(() => {
      const state: STATE_ENV = store$.value.sliceEnv
      return axiosGet$<EnumUI>(
        `/api/excursion-tags/${state.lang}`).pipe(
          map((data) => excursionTagFilled(data.items))
      )
    })
  )

export default combineEpics(excursionFetchEpic)
