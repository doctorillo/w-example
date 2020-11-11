import { ResultKind } from '../../../types/ResultKind'
import { DataBasic, dataBasicInit, fulfillDataBasic } from '../../../types/stores/DataBasic'
import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import {
  excursionSearchClean,
  excursionSearchFetchStart,
  excursionSearchFilled,
  PayloadExcursionSearchClean,
  PayloadExcursionSearchFetchStart,
  PayloadExcursionSearchFilled,
} from './actions'
import { ExcursionCardUI } from '../../../types/bookings/ExcursionCardUI'

export type STATE_SEARCH = DataBasic<ExcursionCardUI>

const cleanR: CaseReducer<STATE_SEARCH, PayloadExcursionSearchClean> = () => dataBasicInit(ResultKind.Undefined)

const startR: CaseReducer<STATE_SEARCH, PayloadExcursionSearchFetchStart> = () => dataBasicInit(ResultKind.Start)

const filledR: CaseReducer<STATE_SEARCH, PayloadExcursionSearchFilled> = (
  _,
  action
) => fulfillDataBasic(action.payload)

export const reducerExcursionSearch = createReducer<STATE_SEARCH>(dataBasicInit<ExcursionCardUI>(ResultKind.Undefined), builder => builder
  .addCase(excursionSearchClean, cleanR)
  .addCase(excursionSearchFetchStart, startR)
  .addCase(excursionSearchFilled, filledR)
)