import {
  AMENITY_CLEAN,
  AMENITY_EXTRA_ENV_UPDATED,
  AMENITY_FETCH,
  AMENITY_FETCH_START,
  AMENITY_FILLED,
} from '../../../ActionType'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { ExtraEnvBaseUpdate } from '../../../../types/cmd/ExtraEnvBaseUpdate'

export const amenityFilled  = createAction<EnumUI[], typeof AMENITY_FILLED>(AMENITY_FILLED)
export type PayloadAmenityFilled = PayloadAction<EnumUI[], typeof AMENITY_FILLED>

export const amenityFetch = createAction<void, typeof AMENITY_FETCH>(AMENITY_FETCH)
export type PayloadAmenityFetch = PayloadAction<void, typeof AMENITY_FETCH>

export const amenityFetchStart  = createAction<void, typeof AMENITY_FETCH_START>(AMENITY_FETCH_START)
export type PayloadAmenityFetchStart = PayloadAction<void, typeof AMENITY_FETCH_START>

export const amenityClean = createAction<void, typeof AMENITY_CLEAN>(AMENITY_CLEAN)
export type PayloadAmenityClean = PayloadAction<void, typeof AMENITY_CLEAN>

export const amenityExtraEnvUpdated = createAction<ExtraEnvBaseUpdate, typeof AMENITY_EXTRA_ENV_UPDATED>(AMENITY_EXTRA_ENV_UPDATED)
export type PayloadExtraEnvUpdated = PayloadAction<ExtraEnvBaseUpdate, typeof AMENITY_EXTRA_ENV_UPDATED>

