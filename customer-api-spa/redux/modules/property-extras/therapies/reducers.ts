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
import {
  PayloadExtraEnvUpdated,
  PayloadTherapyClean,
  PayloadTherapyFetchStart,
  PayloadTherapyFilled,
  therapyClean,
  therapyExtraEnvUpdated,
  therapyFetchStart,
  therapyFilled,
} from './actions'
import { Nullable } from '../../../../types/Nullable'
import { ExtraSelect } from '../../../../types/basic/ExtraSelect'

export type STATE_THERAPY_ENV = Nullable<ExtraSelect<string>>
export type STATE_THERAPY = DataAtom<EnumUI, STATE_THERAPY_ENV>

const sortFn: Sort<EnumUI> = (x: EnumUI, y: EnumUI) =>
  x.label.label.localeCompare(y.label.label)


const cleanR: CaseReducer<STATE_THERAPY, PayloadTherapyClean> = state => cleanDataAtom(state)

const fetchStartR: CaseReducer<STATE_THERAPY, PayloadTherapyFetchStart> = state => startedDataAtom(state)

const filledR: CaseReducer<STATE_THERAPY, PayloadTherapyFilled> = (
  state,
  action,
) => fulfillDataAtom(state, action.payload, null, sortFn)

const extraEnvR: CaseReducer<STATE_THERAPY, PayloadExtraEnvUpdated> = (state, action) => {
  const extraEnv = {
    customerId: action.payload.customerId,
    lang: action.payload.lang,
    selected: null,
  }
  return {...state, extraEnv}
}

export const reducerTherapy = createReducer<STATE_THERAPY>(initDataAtom<EnumUI, STATE_THERAPY_ENV>(ResultKind.Undefined, null), builder => builder
  .addCase(therapyClean, cleanR)
  .addCase(therapyExtraEnvUpdated, extraEnvR)
  .addCase(therapyFetchStart, fetchStartR)
  .addCase(therapyFilled, filledR)
)
