import { EnumUI } from '../../../types/basic/EnumUI'
import { ResultKind } from '../../../types/ResultKind'
import {
  cleanDataAtom,
  DataAtom,
  fulfillDataAtom,
  initDataAtom,
  startedDataAtom,
} from '../../../types/stores/DataAtom'
import { Sort } from '../../../types/stores/Sort'
import {
  excursionTagClean,
  excursionTagExtraEnvUpdated,
  excursionTagFetchStart,
  excursionTagFilled,
  PayloadExcursionTagClean,
  PayloadExcursionTagFetchStart,
  PayloadExcursionTagFilled, PayloadExtraEnvUpdated,
} from './actions'
import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import { Nullable } from '../../../types/Nullable'
import { ExtraSelect } from '../../../types/basic/ExtraSelect'

export type STATE_EXCURSION_TAG_EXTRA = Nullable<ExtraSelect<string>>
export type STATE_EXCURSION_TAG = DataAtom<EnumUI, STATE_EXCURSION_TAG_EXTRA>

const sortFn: Sort<EnumUI> = (x: EnumUI, y: EnumUI) =>
  x.label.label.localeCompare(y.label.label)

const cleanR: CaseReducer<STATE_EXCURSION_TAG, PayloadExcursionTagClean> = (state) =>
  cleanDataAtom(state)

const fetchStartR: CaseReducer<STATE_EXCURSION_TAG, PayloadExcursionTagFetchStart> = (state) => startedDataAtom(state)

const filledR: CaseReducer<STATE_EXCURSION_TAG, PayloadExcursionTagFilled> = (
  state,
  action ,
) => fulfillDataAtom(state, action.payload, null, sortFn)

const extraEnvR: CaseReducer<STATE_EXCURSION_TAG, PayloadExtraEnvUpdated> = (state, action) => {
  const extraEnv = {
    customerId: action.payload.customerId,
    lang: action.payload.lang,
    selected: null,
  }
  return {...state, extraEnv}
}

export const reducerExcursionTag = createReducer<STATE_EXCURSION_TAG>(initDataAtom<EnumUI, STATE_EXCURSION_TAG_EXTRA>(ResultKind.Undefined, null), builder => builder
  .addCase(excursionTagClean, cleanR)
  .addCase(excursionTagExtraEnvUpdated, extraEnvR)
  .addCase(excursionTagFetchStart, fetchStartR)
  .addCase(excursionTagFilled, filledR)
)
