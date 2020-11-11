import { Observable } from 'rxjs'
import { filter, map, mergeMap } from 'rxjs/operators'
import { dummy } from '../../ActionType'
import { axiosGet$ } from '../../axios'
import { QueryResult } from '../../../types/QueryResult'
import { relationFetch, relationFetchCompleted } from './actions'
import { PartyValue } from '../../../types/parties/PartyValue'
import { combineEpics } from 'redux-observable'
import { PartyRelations } from '../../../types/parties/PartyRelations'

export const partyRelationsEpic = (action$: Observable<any>) =>
  action$.pipe(
    filter(relationFetch.match),
    mergeMap((action) => {
      return axiosGet$<PartyValue>(`/api/business/relations/${action.payload.partyId}/${action.payload.ctx}/2`).pipe(
        map((data: QueryResult<PartyValue>) => {
          if (data.hasError || data.size === 0) {
            data.debug.forEach(err => console.log(err))
            return dummy()
          }
          const relations: PartyRelations = {
            partyId: action.payload.partyId,
            ctx: action.payload.ctx,
            suppliers: [],
            customers: data.items
          }
          return relationFetchCompleted(relations)
        })
      )
    }),
  )

export default combineEpics(partyRelationsEpic)