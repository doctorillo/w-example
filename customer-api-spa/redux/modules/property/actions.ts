import {
  PROPERTY_CLEAN,
  PROPERTY_DESCRIPTION_FETCH,
  PROPERTY_DESCRIPTION_FILLED,
  PROPERTY_PRICE_FETCH,
  PROPERTY_PRICE_FILLED,
  PROPERTY_SET,
} from '../../ActionType'
import { PriceUnitUI } from '../../../types/property/prices/PriceUnitUI'
import { PropertyDescriptionUI } from '../../../types/property/PropertyDescriptionUI'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { Nullable } from '../../../types/Nullable'

export const propertyClean = createAction<void, typeof PROPERTY_CLEAN>(PROPERTY_CLEAN)
export type PayloadPropertyClean = PayloadAction<void, typeof PROPERTY_CLEAN>

export type PropertyData = {
  propertyId: string;
  customerId: string;
  supplierId: string;
}

export const propertySet = createAction<PropertyData, typeof PROPERTY_SET>(PROPERTY_SET)
export type PayloadPropertySet = PayloadAction<PropertyData, typeof PROPERTY_SET>

export const propertyPriceFetch = createAction<void, typeof PROPERTY_PRICE_FETCH>(PROPERTY_PRICE_FETCH)
export type PayloadPropertyPriceFetch = PayloadAction<void, typeof PROPERTY_PRICE_FETCH>

export const propertyPriceFilled = createAction<PriceUnitUI[], typeof PROPERTY_PRICE_FILLED>(PROPERTY_PRICE_FILLED)
export type PayloadPropertyPriceFilled = PayloadAction<PriceUnitUI[], typeof PROPERTY_PRICE_FILLED>

export const propertyDescriptionFetch = createAction<void, typeof PROPERTY_DESCRIPTION_FETCH>(PROPERTY_DESCRIPTION_FETCH)
export type PayloadPropertyDescriptionFetch = PayloadAction<void, typeof PROPERTY_DESCRIPTION_FETCH>

export const propertyDescriptionFilled = createAction<Nullable<PropertyDescriptionUI>, typeof PROPERTY_DESCRIPTION_FILLED>(PROPERTY_DESCRIPTION_FILLED)
export type PayloadDescriptionFilled = PayloadAction<Nullable<PropertyDescriptionUI>, typeof PROPERTY_DESCRIPTION_FILLED>