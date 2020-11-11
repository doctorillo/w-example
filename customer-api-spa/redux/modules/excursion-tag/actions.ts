import {
  EXCURSION_TAG_CLEAN,
  EXCURSION_TAG_EXTRA_ENV_UPDATED,
  EXCURSION_TAG_FETCH,
  EXCURSION_TAG_FETCH_START,
  EXCURSION_TAG_FILLED,
} from '../../ActionType'
import { EnumUI } from '../../../types/basic/EnumUI'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { ExtraEnvBaseUpdate } from '../../../types/cmd/ExtraEnvBaseUpdate'

export const excursionTagFilled  = createAction<EnumUI[], typeof EXCURSION_TAG_FILLED>(EXCURSION_TAG_FILLED)
export type PayloadExcursionTagFilled = PayloadAction<EnumUI[], typeof EXCURSION_TAG_FILLED>

export const excursionTagFetch = createAction<void, typeof EXCURSION_TAG_FETCH>(EXCURSION_TAG_FETCH)
export type PayloadExcursionTagFetch = PayloadAction<void, typeof EXCURSION_TAG_FETCH>

export const excursionTagFetchStart  = createAction<void, typeof EXCURSION_TAG_FETCH_START>(EXCURSION_TAG_FETCH_START)
export type PayloadExcursionTagFetchStart = PayloadAction<void, typeof EXCURSION_TAG_FETCH_START>

export const excursionTagClean = createAction<void, typeof EXCURSION_TAG_CLEAN>(EXCURSION_TAG_CLEAN)
export type PayloadExcursionTagClean = PayloadAction<void, typeof EXCURSION_TAG_CLEAN>

export const excursionTagExtraEnvUpdated = createAction<ExtraEnvBaseUpdate, typeof EXCURSION_TAG_EXTRA_ENV_UPDATED>(EXCURSION_TAG_EXTRA_ENV_UPDATED)
export type PayloadExtraEnvUpdated = PayloadAction<ExtraEnvBaseUpdate, typeof EXCURSION_TAG_EXTRA_ENV_UPDATED>

