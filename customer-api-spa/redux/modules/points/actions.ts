import {
  POINT_CLEAN,
  POINT_EXTRA_ENV_UPDATED,
  POINT_FETCH,
  POINT_FETCH_START,
  POINT_FILLED,
  POINT_SELECT,
} from '../../ActionType'
import { PointUI } from '../../../types/geo/PointUI'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { Nullable } from '../../../types/Nullable'
import { ExtraEnvBaseUpdate } from '../../../types/cmd/ExtraEnvBaseUpdate'


export const pointClean = createAction<void, typeof POINT_CLEAN>(POINT_CLEAN)
export type PayloadPointClean = PayloadAction<void, typeof POINT_CLEAN>

export const pointExtraEnvUpdated = createAction<ExtraEnvBaseUpdate, typeof POINT_EXTRA_ENV_UPDATED>(POINT_EXTRA_ENV_UPDATED)
export type PayloadExtraEnvUpdated = PayloadAction<ExtraEnvBaseUpdate, typeof POINT_EXTRA_ENV_UPDATED>

export const pointFetchStart = createAction<void, typeof POINT_FETCH_START>(POINT_FETCH_START)
export type PayloadPointFetchStart = PayloadAction<void, typeof POINT_FETCH_START>

export const pointFetch = createAction<void, typeof POINT_FETCH>(POINT_FETCH)
export type PayloadPointFetch = PayloadAction<void, typeof POINT_FETCH>

export const pointFilled = createAction<PointUI[], typeof POINT_FILLED>(POINT_FILLED)
export type PayloadPointFilled = PayloadAction<PointUI[], typeof POINT_FILLED>

export const pointSelect = createAction<Nullable<string>, typeof POINT_SELECT>(POINT_SELECT)
export type PayloadPointSelect = PayloadAction<Nullable<string>, typeof POINT_SELECT>
