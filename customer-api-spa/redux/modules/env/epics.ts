import { filter, map, mergeMap } from 'rxjs/operators'
import { combineEpics } from 'redux-observable'

import { envCustomerCtxUpdate, envCustomerCtxUpdated, envStateSigned, envStateSignIn } from './actions'
import { dummy } from '../../ActionType'
import { axiosPost$ } from '../../axios'
import { QueryResult } from '../../../types/QueryResult'
import { ContextEnvUI } from '../../../types/contexts/ContextEnvUI'
import { envAsk, EnvState, of } from '../../../types/contexts/EnvState'
import { Observable } from 'rxjs'
import { SignInQ } from '../../../types/contexts/SignInQ'
import { StateWrapper } from '../../index'

/*export const attachSessionEpic = action$ =>
  action$.pipe(
    ofType(ENV_STATE_ATTACH),
    mergeMap((action: EnvStateAttach) => {
      return axiosGet$<string>(
        `/api/attach/${action.payload}`,
        (_: QueryResult<string>) => {
          return dummy()
        },
      )
    }),
  )*/

export const signInEpic = (action$: Observable<any>, state$: StateWrapper) =>
  action$.pipe(
    filter(envStateSignIn.match),
    mergeMap(action => {
      return axiosPost$<SignInQ, ContextEnvUI>('/api/sign-in', action.payload).pipe(
        map((data: QueryResult<ContextEnvUI>, _) => {
          if (data.hasError || data.size === 0) {
            return dummy()
          }
          const nav = state$.value.sliceEnv.navigation
          const ctx: EnvState = of(data.items[0], nav)
          if (!ctx.workspace){
            return dummy()
          }
          return envStateSigned(ctx)
        })
      )
    }),
  )

export const customerContextUpdateEpic = (action$: Observable<any>, state$: StateWrapper) =>
  action$.pipe(
    filter(envCustomerCtxUpdate.match),
    map((action) => {
      const ask = envAsk(state$.value.sliceEnv)
      if (!ask){
        return envCustomerCtxUpdated(action.payload)
      } else {
        if (ask.customerId === action.payload.id && ask.dates?.from === action.payload.dates?.from && ask.dates?.to === action.payload.dates?.to && ask.point?.value === action.payload.point?.value){
          return dummy()
        }
        return envCustomerCtxUpdated(action.payload)
      }
    }))

export default combineEpics(signInEpic, customerContextUpdateEpic)
