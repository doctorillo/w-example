import {
  FACILITY_CLEAN,
  FACILITY_EXTRA_ENV_UPDATED,
  FACILITY_FETCH,
  FACILITY_FETCH_START,
  FACILITY_FILLED,
} from '../../../ActionType'
import { EnumUI } from '../../../../types/basic/EnumUI'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { ExtraEnvBaseUpdate } from '../../../../types/cmd/ExtraEnvBaseUpdate'

export const facilityClean = createAction<void, typeof FACILITY_CLEAN>(FACILITY_CLEAN)
export type PayloadFacilityClean = PayloadAction<void, typeof FACILITY_CLEAN>

export const facilityFilled = createAction<EnumUI[], typeof FACILITY_FILLED>(FACILITY_FILLED)
export type PayloadFacilityFilled = PayloadAction<EnumUI[], typeof FACILITY_FILLED>

export const facilityFetch = createAction<void, typeof FACILITY_FETCH>(FACILITY_FETCH)
export type PayloadFacilityFetch = PayloadAction<void, typeof FACILITY_FETCH>

export const facilityFetchStart = createAction<void, typeof FACILITY_FETCH_START>(FACILITY_FETCH_START)
export type PayloadFacilityFetchStart = PayloadAction<void, typeof FACILITY_FETCH_START>

export const facilityExtraEnvUpdated = createAction<ExtraEnvBaseUpdate, typeof FACILITY_EXTRA_ENV_UPDATED>(FACILITY_EXTRA_ENV_UPDATED)
export type PayloadExtraEnvUpdated = PayloadAction<ExtraEnvBaseUpdate, typeof FACILITY_EXTRA_ENV_UPDATED>