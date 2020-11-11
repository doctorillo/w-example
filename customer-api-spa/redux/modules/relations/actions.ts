import { createAction, PayloadAction } from '@reduxjs/toolkit'
import {
  RELATION_CLEAN,
  RELATION_FETCH,
  RELATION_FETCH_COMPLETED,
  RELATION_FETCH_STARTED,
  RELATION_SELECT,
} from '../../ActionType'
import { RelationFetch } from '../../../types/relations/cmd/RelationFetch'
import { PartyRelations } from '../../../types/parties/PartyRelations'
import { Nullable } from '../../../types/Nullable'

export const relationFetch = createAction<RelationFetch, typeof RELATION_FETCH>(RELATION_FETCH)
export type PayloadRelationFetch = PayloadAction<RelationFetch, typeof RELATION_FETCH>

export const relationFetchStarted = createAction<void, typeof RELATION_FETCH_STARTED>(RELATION_FETCH_STARTED)
export type PayloadRelationFetchStarted = PayloadAction<void, typeof RELATION_FETCH_STARTED>

export const relationFetchCompleted = createAction<PartyRelations, typeof RELATION_FETCH_COMPLETED>(RELATION_FETCH_COMPLETED)
export type PayloadRelationFetchCompleted = PayloadAction<PartyRelations, typeof RELATION_FETCH_COMPLETED>

export const relationSelect = createAction<Nullable<string>, typeof RELATION_SELECT>(RELATION_SELECT)
export type PayloadRelationSelect = PayloadAction<Nullable<string>, typeof RELATION_SELECT>

export const relationClean = createAction<void, typeof RELATION_CLEAN>(RELATION_CLEAN)
export type PayloadRelationClean = PayloadAction<void, typeof RELATION_CLEAN>
