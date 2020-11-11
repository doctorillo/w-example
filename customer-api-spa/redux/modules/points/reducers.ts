import { PointUI } from '../../../types/geo/PointUI'

import { initDataState } from '../../../types/stores/DataState'

import {
  PayloadExtraEnvUpdated,
  PayloadPointClean,
  PayloadPointFilled,
  PayloadPointSelect,
  pointClean,
  pointExtraEnvUpdated,
  pointFilled,
  pointSelect,
} from './actions'
import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import { cleanDataAtom, DataAtom, fulfillDataAtom } from '../../../types/stores/DataAtom'
import { Nullable } from '../../../types/Nullable'
import { Uuid } from '../../../types/basic/Uuid'

export type STATE_POINT_EXTRA = {
  selected: Nullable<Uuid>;
}
export type STATE_POINT = DataAtom<PointUI, STATE_POINT_EXTRA>

const cleanR: CaseReducer<STATE_POINT, PayloadPointClean> = state => cleanDataAtom(state)

const fillR: CaseReducer<STATE_POINT, PayloadPointFilled> = (state, action) => fulfillDataAtom(state, action.payload, null, null)

const extraEnvR: CaseReducer<STATE_POINT, PayloadExtraEnvUpdated> = (state, action) => {
  const extraEnv = {
    customerId: action.payload.customerId,
    lang: action.payload.lang,
    selected: null,
  }
  return {...state, extraEnv}
}

const selectR: CaseReducer<STATE_POINT, PayloadPointSelect> = (state, action) => {
  const extraEnv = !state.extraEnv ? null : { ...state.extraEnv, selected: action.payload }
  return {...state, extraEnv}
}

export const reducerPoints = createReducer<STATE_POINT>(initDataState<PointUI, STATE_POINT_EXTRA>({
    selected: null
  }), builder => builder
  .addCase(pointClean, cleanR)
  .addCase(pointFilled, fillR)
  .addCase(pointExtraEnvUpdated, extraEnvR)
  .addCase(pointSelect, selectR),
)
