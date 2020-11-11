import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import { PartyRelations } from '../../../types/parties/PartyRelations'
import { cleanDataItem, DataItem, initDataItem } from '../../../types/stores/DataItem'
import {
  PayloadRelationClean,
  PayloadRelationFetchCompleted,
  PayloadRelationFetchStarted,
  relationClean,
  relationFetchCompleted,
  relationFetchStarted,
} from './actions'
import { ResultKind } from '../../../types/ResultKind'
import { Nullable } from '../../../types/Nullable'
import { Sort } from '../../../types/stores/Sort'
import { PartyValue } from '../../../types/parties/PartyValue'

export type STATE_RELATIONS = DataItem<PartyRelations, Nullable<string>>

const cleanR: CaseReducer<STATE_RELATIONS, PayloadRelationClean> = (state) => cleanDataItem(state)

const startedR: CaseReducer<STATE_RELATIONS, PayloadRelationFetchStarted> = (state) => ({...state, status: ResultKind.Start})

const sortParty: Sort<PartyValue> = (x, y) => x.name.localeCompare(y.name)

const completedR: CaseReducer<STATE_RELATIONS, PayloadRelationFetchCompleted> = (state, action) => {
  const spl = action.payload.suppliers.sort(sortParty)
  const cst = action.payload.customers.sort(sortParty)
  const relation = {...action.payload, suppliers: spl, customers: cst}
  return ({...state, status: ResultKind.Completed, item: relation, extraEnv: null})
}

export const reducerRelation = createReducer<STATE_RELATIONS>(initDataItem<PartyRelations, Nullable<string>>(ResultKind.Undefined, null, null), builder => builder
  .addCase(relationClean, cleanR)
  .addCase(relationFetchStarted, startedR)
  .addCase(relationFetchCompleted, completedR)
)