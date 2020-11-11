import { axiosGet$ } from '../../../axios'
import { combineEpics } from 'redux-observable'
import { filter, map, mergeMap } from 'rxjs/operators'
import { therapyFetch, therapyFilled } from './actions'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { STATE_ENV } from '../../env/reducers'
import { Observable } from 'rxjs'
import { StateWrapper } from '../../../index'

const therapyFetchEpic = (action$: Observable<any>, store$: StateWrapper) =>
  action$.pipe(
    filter(therapyFetch.match),
    mergeMap(() => {
      const state: STATE_ENV = store$.value.sliceEnv
      return axiosGet$<EnumUI>(
        `/api/therapy-procedures/${state.lang}`).pipe(
          map(data => therapyFilled(data.items))
      )})
  )

export default combineEpics(
  therapyFetchEpic
)
