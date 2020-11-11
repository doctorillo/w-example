import { PROPERTY_SEARCH_CLEAN, PROPERTY_SEARCH_FETCH, PROPERTY_SEARCH_FETCH_START, PROPERTY_SEARCH_FILLED } from '../../ActionType'
import { PropertyCardUI } from '../../../types/bookings/PropertyCardUI'
import { createAction, PayloadAction } from '@reduxjs/toolkit'

export const propertySearchClean = createAction<void, typeof PROPERTY_SEARCH_CLEAN>(PROPERTY_SEARCH_CLEAN)
export type PayloadPropertySearchClean = PayloadAction<void, typeof PROPERTY_SEARCH_CLEAN>

export const propertySearchFetch = createAction<void, typeof PROPERTY_SEARCH_FETCH>(PROPERTY_SEARCH_FETCH)
export type PayloadPropertySearchFetch = PayloadAction<void, typeof PROPERTY_SEARCH_FETCH>

export const propertySearchFetchStart = createAction<void, typeof PROPERTY_SEARCH_FETCH_START>(PROPERTY_SEARCH_FETCH_START)
export type PayloadPropertySearchFetchStart = PayloadAction<void, typeof PROPERTY_SEARCH_FETCH_START>

export const propertySearchFilled = createAction<PropertyCardUI[], typeof PROPERTY_SEARCH_FILLED>(PROPERTY_SEARCH_FILLED)
export type PayloadPropertySearchFilled = PayloadAction<PropertyCardUI[], typeof PROPERTY_SEARCH_FILLED>
