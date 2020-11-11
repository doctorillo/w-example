import {
  AMENITY_EXTRA_ENV_UPDATED,
  INDICATION_CLEAN,
  INDICATION_FETCH,
  INDICATION_FETCH_START,
  INDICATION_FILLED,
} from '../../../ActionType'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { ExtraEnvBaseUpdate } from '../../../../types/cmd/ExtraEnvBaseUpdate'

export const indicationFilled  = createAction<EnumUI[], typeof INDICATION_FILLED>(INDICATION_FILLED)
export type PayloadIndicationFilled = PayloadAction<EnumUI[], typeof INDICATION_FILLED>

export const indicationFetch = createAction<void, typeof INDICATION_FETCH>(INDICATION_FETCH)
export type PayloadIndicationFetch = PayloadAction<void, typeof INDICATION_FETCH>

export const indicationFetchStart  = createAction<void, typeof INDICATION_FETCH_START>(INDICATION_FETCH_START)
export type PayloadIndicationFetchStart = PayloadAction<void, typeof INDICATION_FETCH_START>

export const indicationClean = createAction<void, typeof INDICATION_CLEAN>(INDICATION_CLEAN)
export type PayloadIndicationClean = PayloadAction<void, typeof INDICATION_CLEAN>

export const indicationExtraEnvUpdated = createAction<ExtraEnvBaseUpdate, typeof AMENITY_EXTRA_ENV_UPDATED>(AMENITY_EXTRA_ENV_UPDATED)
export type PayloadIndicationExtraEnvUpdated = PayloadAction<ExtraEnvBaseUpdate, typeof AMENITY_EXTRA_ENV_UPDATED>

