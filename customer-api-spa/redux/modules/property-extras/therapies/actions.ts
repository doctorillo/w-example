import {
  THERAPY_CLEAN,
  THERAPY_EXTRA_ENV_UPDATED,
  THERAPY_FETCH,
  THERAPY_FETCH_START,
  THERAPY_FILLED,
} from '../../../ActionType'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { ExtraEnvBaseUpdate } from '../../../../types/cmd/ExtraEnvBaseUpdate'

export const therapyClean = createAction<void, typeof THERAPY_CLEAN>(THERAPY_CLEAN)
export type PayloadTherapyClean = PayloadAction<void, typeof THERAPY_CLEAN>

export const therapyFetch = createAction<void, typeof THERAPY_FETCH>(THERAPY_FETCH)
export type PayloadTherapyFetch = PayloadAction<void, typeof THERAPY_FETCH>

export const therapyFetchStart = createAction<void, typeof THERAPY_FETCH_START>(THERAPY_FETCH_START)
export type PayloadTherapyFetchStart = PayloadAction<void, typeof THERAPY_FETCH_START>

export const therapyFilled = createAction<EnumUI[], typeof THERAPY_FILLED>(THERAPY_FILLED)
export type PayloadTherapyFilled = PayloadAction<EnumUI[], typeof THERAPY_FILLED>

export const therapyExtraEnvUpdated = createAction<ExtraEnvBaseUpdate, typeof THERAPY_EXTRA_ENV_UPDATED>(THERAPY_EXTRA_ENV_UPDATED)
export type PayloadExtraEnvUpdated = PayloadAction<ExtraEnvBaseUpdate, typeof THERAPY_EXTRA_ENV_UPDATED>
