import { EnvProperty, initEnvProperty } from '../../../types/bookings/EnvProperty'
import {
  PayloadDescriptionFilled,
  PayloadPropertyClean,
  PayloadPropertyDescriptionFetch,
  PayloadPropertyPriceFetch,
  PayloadPropertyPriceFilled,
  PayloadPropertySet,
  propertyClean,
  propertyDescriptionFetch,
  propertyDescriptionFilled,
  propertyPriceFetch,
  propertyPriceFilled,
  propertySet,
} from './actions'
import { ResultKind } from '../../../types/ResultKind'
import { v4 as uuid } from 'uuid'
import { CaseReducer, createReducer } from '@reduxjs/toolkit'

export type STATE_PROPERTY = EnvProperty

const cleanR: CaseReducer<STATE_PROPERTY, PayloadPropertyClean> = state => initEnvProperty(state.id)

const setR: CaseReducer<STATE_PROPERTY, PayloadPropertySet> = (_: STATE_PROPERTY, action: PayloadPropertySet) => ({
  id: action.payload.propertyId,
  supplierId: action.payload.supplierId,
  customerId: action.payload.customerId,
  description: null,
  descriptionStatus: ResultKind.Undefined,
  prices: [],
  priceStatus: ResultKind.Undefined,
})

const startDescriptionR: CaseReducer<STATE_PROPERTY, PayloadPropertyDescriptionFetch> = state => ({
  ...state,
  description: null,
  descriptionStatus: ResultKind.Start,
})

const startPriceR: CaseReducer<STATE_PROPERTY, PayloadPropertyPriceFetch> = state => ({
  ...state,
  prices: [],
  priceStatus: ResultKind.Start,
})

const filledDescriptionR: CaseReducer<STATE_PROPERTY, PayloadDescriptionFilled> = (state,
                                                                                   action,
) => ({ ...state, description: action.payload, descriptionStatus: ResultKind.Completed })

const filledPriceR: CaseReducer<STATE_PROPERTY, PayloadPropertyPriceFilled> = (
  state,
  action,
) => ({ ...state, prices: action.payload, priceStatus: ResultKind.Completed })

export const reducerProperty = createReducer<STATE_PROPERTY>(initEnvProperty(uuid()), builder => builder
  .addCase(propertyClean, cleanR)
  .addCase(propertySet, setR)
  .addCase(propertyPriceFetch, startPriceR)
  .addCase(propertyDescriptionFetch, startDescriptionR)
  .addCase(propertyPriceFilled, filledPriceR)
  .addCase(propertyDescriptionFilled, filledDescriptionR)
)
