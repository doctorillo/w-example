import {
  facilityClean,
  facilityExtraEnvUpdated,
  facilityFetchStart,
  facilityFilled,
  PayloadExtraEnvUpdated,
  PayloadFacilityClean,
  PayloadFacilityFetchStart,
  PayloadFacilityFilled,
} from './actions'
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
import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import { Nullable } from '../../../../types/Nullable'
import { ExtraSelect } from '../../../../types/basic/ExtraSelect'

export type STATE_FACILITY_EXTRA = Nullable<ExtraSelect<string>>
export type STATE_FACILITY = DataAtom<EnumUI, STATE_FACILITY_EXTRA>

const sortFn: Sort<EnumUI> = (x: EnumUI, y: EnumUI) =>
  x.label.label.localeCompare(y.label.label)


const cleanR: CaseReducer<STATE_FACILITY, PayloadFacilityClean> = state => cleanDataAtom(state)

const fetchStartR: CaseReducer<STATE_FACILITY, PayloadFacilityFetchStart> = state => startedDataAtom(state)

const filledR: CaseReducer<STATE_FACILITY, PayloadFacilityFilled> = (
  state ,
  action,
) => fulfillDataAtom(state, action.payload, null, sortFn)

const extraEnvR: CaseReducer<STATE_FACILITY, PayloadExtraEnvUpdated> = (state, action) => {
  const extraEnv = {
    customerId: action.payload.customerId,
    lang: action.payload.lang,
    selected: null,
  }
  return {...state, extraEnv}
}

export const reducerFacility = createReducer<STATE_FACILITY>(initDataAtom<EnumUI, STATE_FACILITY_EXTRA>(ResultKind.Undefined, null), builder => builder
  .addCase(facilityClean, cleanR)
  .addCase(facilityExtraEnvUpdated, extraEnvR)
  .addCase(facilityFetchStart, fetchStartR)
  .addCase(facilityFilled, filledR)
)
