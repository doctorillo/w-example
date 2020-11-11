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
  indicationClean,
  indicationExtraEnvUpdated,
  indicationFetchStart,
  indicationFilled,
  PayloadIndicationClean,
  PayloadIndicationExtraEnvUpdated,
  PayloadIndicationFetchStart,
  PayloadIndicationFilled,
} from './actions'
import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import { Nullable } from '../../../../types/Nullable'
import { ExtraSelect } from '../../../../types/basic/ExtraSelect'

export type STATE_INDICATION_EXTRA = Nullable<ExtraSelect<string>>
export type STATE_INDICATION = DataAtom<EnumUI, STATE_INDICATION_EXTRA>

const sortFn: Sort<EnumUI> = (x: EnumUI, y: EnumUI) =>
  x.label.label.localeCompare(y.label.label)

const cleanR: CaseReducer<STATE_INDICATION, PayloadIndicationClean> = (state) =>
  cleanDataAtom(state)

const fetchStartR: CaseReducer<STATE_INDICATION, PayloadIndicationFetchStart> = (state) => startedDataAtom(state)

const filledR: CaseReducer<STATE_INDICATION, PayloadIndicationFilled> = (
  state,
  action ,
) => fulfillDataAtom(state, action.payload, null, sortFn)

const extraEnvR: CaseReducer<STATE_INDICATION, PayloadIndicationExtraEnvUpdated> = (state, action) => {
  const extraEnv = {
    customerId: action.payload.customerId,
    lang: action.payload.lang,
    selected: null,
  }
  return {...state, extraEnv}
}

export const reducerIndication = createReducer(initDataAtom<EnumUI, STATE_INDICATION_EXTRA>(ResultKind.Undefined, null), builder => builder
  .addCase(indicationClean, cleanR)
  .addCase(indicationExtraEnvUpdated, extraEnvR)
  .addCase(indicationFetchStart, fetchStartR)
  .addCase(indicationFilled, filledR)
)
