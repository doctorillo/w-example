import { ResultKind } from '../../../types/ResultKind'
import { PropertyCardUI } from '../../../types/bookings/PropertyCardUI'
import { DataBasic, dataBasicInit, fulfillDataBasic } from '../../../types/stores/DataBasic'
import { CaseReducer, createReducer } from '@reduxjs/toolkit'
import {
  PayloadPropertySearchClean,
  PayloadPropertySearchFetchStart,
  PayloadPropertySearchFilled,
  propertySearchClean,
  propertySearchFetchStart,
  propertySearchFilled,
} from './actions'

export type STATE_SEARCH = DataBasic<PropertyCardUI>

const cleanR: CaseReducer<STATE_SEARCH, PayloadPropertySearchClean> = () => dataBasicInit(ResultKind.Undefined)

const startR: CaseReducer<STATE_SEARCH, PayloadPropertySearchFetchStart> = () => dataBasicInit(ResultKind.Start)

const filledR: CaseReducer<STATE_SEARCH, PayloadPropertySearchFilled> = (
  _,
  action
) => fulfillDataBasic(action.payload)

export const reducerPropertySearch = createReducer<STATE_SEARCH>(dataBasicInit<PropertyCardUI>(ResultKind.Undefined), builder => builder
  .addCase(propertySearchClean, cleanR)
  .addCase(propertySearchFetchStart, startR)
  .addCase(propertySearchFilled, filledR)
)