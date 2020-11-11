import {
  EXCURSION_SEARCH_CLEAN,
  EXCURSION_SEARCH_FETCH,
  EXCURSION_SEARCH_FETCH_START,
  EXCURSION_SEARCH_FILLED,
} from '../../ActionType'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { ExcursionCardUI } from '../../../types/bookings/ExcursionCardUI'

export const excursionSearchClean = createAction<void, typeof EXCURSION_SEARCH_CLEAN>(EXCURSION_SEARCH_CLEAN)
export type PayloadExcursionSearchClean = PayloadAction<void, typeof EXCURSION_SEARCH_CLEAN>

export const excursionSearchFetch = createAction<void, typeof EXCURSION_SEARCH_FETCH>(EXCURSION_SEARCH_FETCH)
export type PayloadExcursionSearchFetch = PayloadAction<void, typeof EXCURSION_SEARCH_FETCH>

export const excursionSearchFetchStart = createAction<void, typeof EXCURSION_SEARCH_FETCH_START>(EXCURSION_SEARCH_FETCH_START)
export type PayloadExcursionSearchFetchStart = PayloadAction<void, typeof EXCURSION_SEARCH_FETCH_START>

export const excursionSearchFilled = createAction<ExcursionCardUI[], typeof EXCURSION_SEARCH_FILLED>(EXCURSION_SEARCH_FILLED)
export type PayloadExcursionSearchFilled = PayloadAction<ExcursionCardUI[], typeof EXCURSION_SEARCH_FILLED>