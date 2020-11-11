import { EnumUI } from '../../../../types/basic/EnumUI'
import { ResultKind } from '../../../../types/ResultKind'
import {
  cleanDataAtom,
  DataAtom,
  fulfillDataAtom,
  initDataAtom,
  startedDataAtom,
} from '../../../../types/stores/DataAtom'
import { Sort } from '../../../../types/stores/Sort'
import {
  amenityClean,
  amenityExtraEnvUpdated,
  amenityFetchStart,
  amenityFilled,
  PayloadAmenityClean,
  PayloadAmenityFetchStart,
  PayloadAmenityFilled, PayloadExtraEnvUpdated,
} from './actions'
import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import { Nullable } from '../../../../types/Nullable'
import { ExtraSelect } from '../../../../types/basic/ExtraSelect'

export type STATE_AMENITY_EXTRA = Nullable<ExtraSelect<string>>
export type STATE_AMENITY = DataAtom<EnumUI, STATE_AMENITY_EXTRA>

const sortFn: Sort<EnumUI> = (x: EnumUI, y: EnumUI) =>
  x.label.label.localeCompare(y.label.label)

const cleanR: CaseReducer<STATE_AMENITY, PayloadAmenityClean> = (state) =>
  cleanDataAtom(state)

const fetchStartR: CaseReducer<STATE_AMENITY, PayloadAmenityFetchStart> = (state) => startedDataAtom(state)

const filledR: CaseReducer<STATE_AMENITY, PayloadAmenityFilled> = (
  state,
  action ,
) => fulfillDataAtom(state, action.payload, null, sortFn)

const extraEnvR: CaseReducer<STATE_AMENITY, PayloadExtraEnvUpdated> = (state, action) => {
  const extraEnv = {
    customerId: action.payload.customerId,
    lang: action.payload.lang,
    selected: null,
  }
  return {...state, extraEnv}
}

export const reducerAmenity = createReducer<STATE_AMENITY>(initDataAtom<EnumUI, STATE_AMENITY_EXTRA>(ResultKind.Undefined, null), builder => builder
  .addCase(amenityClean, cleanR)
  .addCase(amenityExtraEnvUpdated, extraEnvR)
  .addCase(amenityFetchStart, fetchStartR)
  .addCase(amenityFilled, filledR)
)
