import {
  PLANNER_SESSION_CLEAN,
  PLANNER_SESSION_CLIENT_SET_BIRTH_DAY,
  PLANNER_SESSION_CLIENT_SET_FIRST_NAME,
  PLANNER_SESSION_CLIENT_SET_GENDER,
  PLANNER_SESSION_CLIENT_SET_LAST_NAME,
  PLANNER_SESSION_CLIENT_SET_PASSPORT_EXPIRED,
  PLANNER_SESSION_CLIENT_SET_PASSPORT_NUMBER,
  PLANNER_SESSION_CLIENT_SET_PASSPORT_SERIAL,
  PLANNER_SESSION_CLIENT_SET_PASSPORT_STATE,
  PLANNER_SESSION_CREATE,
  PLANNER_SESSION_CREATE_FORM_TOGGLE,
  PLANNER_SESSION_CREATED,
  PLANNER_SESSION_EXCURSION_FILTER_RESET,
  PLANNER_SESSION_EXCURSION_FILTER_SET_DATES,
  PLANNER_SESSION_EXCURSION_FILTER_SET_NAME,
  PLANNER_SESSION_EXCURSION_FILTER_SET_PRICE,
  PLANNER_SESSION_EXCURSION_FILTER_SET_TAGS,
  PLANNER_SESSION_EXCURSION_SET_EXCURSION_DATES,
  PLANNER_SESSION_EXCURSION_SET_MAX_ITEMS,
  PLANNER_SESSION_EXCURSION_UPDATE_EXCURSION_DATE,
  PLANNER_SESSION_FETCH,
  PLANNER_SESSION_FETCH_COMPLETED,
  PLANNER_SESSION_HEAD_UPDATED,
  PLANNER_SESSION_POINT_CREATE,
  PLANNER_SESSION_POINT_EXCURSION_VARIANT_ADD,
  PLANNER_SESSION_POINT_EXCURSION_VARIANT_REMOVE,
  PLANNER_SESSION_POINT_EXCURSION_VARIANT_SELECT,
  PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_ADD,
  PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_REMOVE,
  PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_SELECT,
  PLANNER_SESSION_POINT_REMOVE,
  PLANNER_SESSION_POINT_SELECT,
  PLANNER_SESSION_PROPERTY_FILTER_RESET,
  PLANNER_SESSION_PROPERTY_FILTER_SET_AMENITIES,
  PLANNER_SESSION_PROPERTY_FILTER_SET_BOARDINGS,
  PLANNER_SESSION_PROPERTY_FILTER_SET_FACILITIES,
  PLANNER_SESSION_PROPERTY_FILTER_SET_INDICATIONS,
  PLANNER_SESSION_PROPERTY_FILTER_SET_MEDICALS,
  PLANNER_SESSION_PROPERTY_FILTER_SET_NAME,
  PLANNER_SESSION_PROPERTY_FILTER_SET_PRICE,
  PLANNER_SESSION_PROPERTY_FILTER_SET_STAR,
  PLANNER_SESSION_PROPERTY_FILTER_SET_STOP,
  PLANNER_SESSION_PROPERTY_FILTER_SET_THERAPIES,
  PLANNER_SESSION_PROPERTY_SET_MAX_ITEMS,
  PLANNER_SESSION_PROPERTY_SET_PRICE_VIEW_MODE,
  PLANNER_SESSION_REMOVE,
  PLANNER_SESSION_REMOVED,
  PLANNER_SESSION_SELECT,
  PLANNER_SESSION_SELECTED, PLANNER_SESSION_SET_BOOKING_STEP,
  PLANNER_SESSION_TOGGLE_PREVIEW,
  PLANNER_SESSION_UPDATE,
} from '../../ActionType'
import { PlannerSession, PlannerSessionId } from '../../../types/planner/PlannerSession'
import { PriceViewMode } from '../../../types/PriceViewMode'
import { PlannerAccommodationVariantAdd } from '../../../types/planner/cmd/PlannerAccommodationVariantAdd'
import { PlannerAccommodationVariantRemove } from '../../../types/planner/cmd/PlannerAccommodationVariantRemove'
import { GenderItem } from '../../../types/parties/GenderItem'
import { PlannerAccommodationVariantSelect } from '../../../types/planner/cmd/PlannerAccommodationVariantSelect'
import { createAction, PayloadAction } from '@reduxjs/toolkit'
import { Nullable } from '../../../types/Nullable'
import { KeyOptionValue } from '../../../types/KeyOptionValue'
import { PlannerSessionCreate } from '../../../types/planner/cmd/PlannerSessionCreate'
import { PlannerPointCreate } from '../../../types/planner/cmd/PlannerPointCreate'
import { PlannerSessionUpdate } from '../../../types/planner/cmd/PlannerSessionUpdate'
import { Uuid } from '../../../types/basic/Uuid'
import { PlannerExcursionVariantAdd } from '../../../types/planner/cmd/PlannerExcursionVariantAdd'
import { PlannerExcursionVariantRemove } from '../../../types/planner/cmd/PlannerExcursionVariantRemove'
import { PlannerExcursionVariantSelect } from '../../../types/planner/cmd/PlannerExcursionVariantSelect'
import { PlannerExcursionDate } from '../../../types/planner/PlannerExcursionDates'
import { DateString } from '../../../types/basic/DateString'


export const plannerClean = createAction<void, typeof PLANNER_SESSION_CLEAN>(PLANNER_SESSION_CLEAN)
export type PayloadPlannerClean = PayloadAction<void, typeof PLANNER_SESSION_CLEAN>

export const plannerFetch = createAction<string, typeof PLANNER_SESSION_FETCH>(PLANNER_SESSION_FETCH)
export type PayloadPlannerFetch = PayloadAction<string, typeof PLANNER_SESSION_FETCH>

export const plannerFetchCompleted = createAction<PlannerSession[], typeof PLANNER_SESSION_FETCH_COMPLETED>(PLANNER_SESSION_FETCH_COMPLETED)
export type PayloadPlannerFetchCompleted = PayloadAction<PlannerSession[], typeof PLANNER_SESSION_FETCH_COMPLETED>

export const plannerCreateFormView = createAction<boolean, typeof PLANNER_SESSION_CREATE_FORM_TOGGLE>(PLANNER_SESSION_CREATE_FORM_TOGGLE)
export type PayloadPlannerCreateFormView = PayloadAction<boolean, typeof PLANNER_SESSION_CREATE_FORM_TOGGLE>

export const plannerCreate = createAction<PlannerSessionCreate, typeof PLANNER_SESSION_CREATE>(PLANNER_SESSION_CREATE)
export type PayloadPlannerCreate = PayloadAction<PlannerSessionCreate, typeof PLANNER_SESSION_CREATE>

export const plannerRemove = createAction<string, typeof PLANNER_SESSION_REMOVE>(PLANNER_SESSION_REMOVE)
export type PayloadPlannerRemove = PayloadAction<string, typeof PLANNER_SESSION_REMOVE>

export const plannerRemoved = createAction<string, typeof PLANNER_SESSION_REMOVED>(PLANNER_SESSION_REMOVED)
export type PayloadPlannerRemoved = PayloadAction<string, typeof PLANNER_SESSION_REMOVED>

export const plannerSelect = createAction<Nullable<PlannerSessionId>, typeof PLANNER_SESSION_SELECT>(PLANNER_SESSION_SELECT)
export type PayloadPlannerSelect = PayloadAction<Nullable<PlannerSessionId>, typeof PLANNER_SESSION_SELECT>

export const plannerSelected = createAction<Nullable<Uuid>, typeof PLANNER_SESSION_SELECTED>(PLANNER_SESSION_SELECTED)
export type PayloadPlannerSelected = PayloadAction<Nullable<Uuid>, typeof PLANNER_SESSION_SELECTED>

export const plannerToggle = createAction<boolean, typeof PLANNER_SESSION_TOGGLE_PREVIEW>(PLANNER_SESSION_TOGGLE_PREVIEW)
export type PayloadPlannerToggle = PayloadAction<boolean, typeof PLANNER_SESSION_TOGGLE_PREVIEW>

export const plannerCreated = createAction<PlannerSession, typeof PLANNER_SESSION_CREATED>(PLANNER_SESSION_CREATED)
export type PayloadPlannerCreated = PayloadAction<PlannerSession, typeof PLANNER_SESSION_CREATED>

export const plannerUpdate = createAction<PlannerSessionUpdate, typeof PLANNER_SESSION_UPDATE>(PLANNER_SESSION_UPDATE)
export type PayloadPlannerUpdate = PayloadAction<PlannerSessionUpdate, typeof PLANNER_SESSION_UPDATE>

export const plannerUpdated = createAction<PlannerSession, typeof PLANNER_SESSION_HEAD_UPDATED>(PLANNER_SESSION_HEAD_UPDATED)
export type PayloadPlannerUpdated = PayloadAction<PlannerSession, typeof PLANNER_SESSION_HEAD_UPDATED>

export const plannerPointCreate = createAction<PlannerPointCreate, typeof PLANNER_SESSION_POINT_CREATE>(PLANNER_SESSION_POINT_CREATE)
export type PayloadPlannerPointCreate = PayloadAction<PlannerPointCreate, typeof PLANNER_SESSION_POINT_CREATE>

export const plannerSetBookingStep = createAction<number, typeof PLANNER_SESSION_SET_BOOKING_STEP>(PLANNER_SESSION_SET_BOOKING_STEP)
export type PayloadPlannerSetBookingStep = PayloadAction<number, typeof PLANNER_SESSION_SET_BOOKING_STEP>

export const plannerPointSelect = createAction<string, typeof PLANNER_SESSION_POINT_SELECT>(PLANNER_SESSION_POINT_SELECT)
export type PayloadPlannerPointSelect = PayloadAction<string, typeof PLANNER_SESSION_POINT_SELECT>

export const plannerPointRemove = createAction<string, typeof PLANNER_SESSION_POINT_REMOVE>(PLANNER_SESSION_POINT_REMOVE)
export type PayloadPlannerPointRemove = PayloadAction<string, typeof PLANNER_SESSION_POINT_REMOVE>

// PROPERTIES

export const plannerPropertyAddVariant = createAction<PlannerAccommodationVariantAdd, typeof PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_ADD>(PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_ADD)
export type PayloadPlannerPropertyVariantAdd = PayloadAction<PlannerAccommodationVariantAdd, typeof PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_ADD>

export const plannerPropertyRemoveVariant = createAction<PlannerAccommodationVariantRemove, typeof PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_REMOVE>(PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_REMOVE)
export type PayloadPlannerPropertyVariantRemove = PayloadAction<PlannerAccommodationVariantRemove, typeof PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_REMOVE>

export const plannerPropertySelectVariant = createAction<PlannerAccommodationVariantSelect, typeof PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_SELECT>(PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_SELECT)
export type PayloadPlannerPropertyVariant = PayloadAction<PlannerAccommodationVariantSelect, typeof PLANNER_SESSION_POINT_PROPERTY_ACCOMMODATION_VARIANT_SELECT>

export const plannerPropertyFilterSetStop = createAction<boolean, typeof PLANNER_SESSION_PROPERTY_FILTER_SET_STOP>(PLANNER_SESSION_PROPERTY_FILTER_SET_STOP)
export type PayloadPlannerPropertyFilterStop = PayloadAction<boolean, typeof PLANNER_SESSION_PROPERTY_FILTER_SET_STOP>

export const plannerPropertyFilterSetStar = createAction<number[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_STAR>(PLANNER_SESSION_PROPERTY_FILTER_SET_STAR)
export type PayloadPlannerPropertyFilterStar = PayloadAction<number[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_STAR>

export const plannerPropertyFilterSetName = createAction<Nullable<string>, typeof PLANNER_SESSION_PROPERTY_FILTER_SET_NAME>(PLANNER_SESSION_PROPERTY_FILTER_SET_NAME)
export type PayloadPlannerPropertyFilterName = PayloadAction<Nullable<string>, typeof PLANNER_SESSION_PROPERTY_FILTER_SET_NAME>

export const plannerPropertyFilterSetPrice = createAction<number[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_PRICE>(PLANNER_SESSION_PROPERTY_FILTER_SET_PRICE)
export type PayloadPlannerPropertyFilterPrice = PayloadAction<number[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_PRICE>

export const plannerPropertyFilterSetAmenities = createAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_AMENITIES>(PLANNER_SESSION_PROPERTY_FILTER_SET_AMENITIES)
export type PayloadPlannerPropertyFilterAmenities = PayloadAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_AMENITIES>

export const plannerPropertyFilterSetFacilities = createAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_FACILITIES>(PLANNER_SESSION_PROPERTY_FILTER_SET_FACILITIES)
export type PayloadPlannerPropertyFilterFacilities = PayloadAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_FACILITIES>

export const plannerPropertyFilterSetMedicals = createAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_MEDICALS>(PLANNER_SESSION_PROPERTY_FILTER_SET_MEDICALS)
export type PayloadPlannerPropertyFilterMedicals = PayloadAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_MEDICALS>

export const plannerPropertyFilterSetIndications = createAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_INDICATIONS>(PLANNER_SESSION_PROPERTY_FILTER_SET_INDICATIONS)
export type PayloadPlannerPropertyFilterIndications = PayloadAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_INDICATIONS>

export const plannerPropertyFilterSetTherapies = createAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_THERAPIES>(PLANNER_SESSION_PROPERTY_FILTER_SET_THERAPIES)
export type PayloadPlannerPropertyFilterTherapies = PayloadAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_THERAPIES>

export const plannerPropertyFilterSetBoardings = createAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_BOARDINGS>(PLANNER_SESSION_PROPERTY_FILTER_SET_BOARDINGS)
export type PayloadPlannerPropertyFilterBoardings = PayloadAction<string[], typeof PLANNER_SESSION_PROPERTY_FILTER_SET_BOARDINGS>

export const plannerPropertyFilterReset = createAction<void, typeof PLANNER_SESSION_PROPERTY_FILTER_RESET>(PLANNER_SESSION_PROPERTY_FILTER_RESET)
export type PayloadPlannerPropertyFilterReset = PayloadAction<void, typeof PLANNER_SESSION_PROPERTY_FILTER_RESET>

export const plannerPropertySetMaxItems = createAction<number, typeof PLANNER_SESSION_PROPERTY_SET_MAX_ITEMS>(PLANNER_SESSION_PROPERTY_SET_MAX_ITEMS)
export type PayloadPlannerPropertyMaxItems = PayloadAction<number, typeof PLANNER_SESSION_PROPERTY_SET_MAX_ITEMS>

export const plannerPropertySetPriceMode = createAction<PriceViewMode, typeof PLANNER_SESSION_PROPERTY_SET_PRICE_VIEW_MODE>(PLANNER_SESSION_PROPERTY_SET_PRICE_VIEW_MODE)
export type PayloadPlannerPropertyPriceMode = PayloadAction<PriceViewMode, typeof PLANNER_SESSION_PROPERTY_SET_PRICE_VIEW_MODE>

// EXCURSIONS

export const plannerExcursionAddVariant = createAction<PlannerExcursionVariantAdd, typeof PLANNER_SESSION_POINT_EXCURSION_VARIANT_ADD>(PLANNER_SESSION_POINT_EXCURSION_VARIANT_ADD)
export type PayloadPlannerExcursionVariantAdd = PayloadAction<PlannerExcursionVariantAdd, typeof PLANNER_SESSION_POINT_EXCURSION_VARIANT_ADD>

export const plannerExcursionRemoveVariant = createAction<PlannerExcursionVariantRemove, typeof PLANNER_SESSION_POINT_EXCURSION_VARIANT_REMOVE>(PLANNER_SESSION_POINT_EXCURSION_VARIANT_REMOVE)
export type PayloadPlannerExcursionVariantRemove = PayloadAction<PlannerExcursionVariantRemove, typeof PLANNER_SESSION_POINT_EXCURSION_VARIANT_REMOVE>

export const plannerExcursionSelectVariant = createAction<PlannerExcursionVariantSelect, typeof PLANNER_SESSION_POINT_EXCURSION_VARIANT_SELECT>(PLANNER_SESSION_POINT_EXCURSION_VARIANT_SELECT)
export type PayloadPlannerExcursionVariant = PayloadAction<PlannerExcursionVariantSelect, typeof PLANNER_SESSION_POINT_EXCURSION_VARIANT_SELECT>

export const plannerExcursionFilterSetName = createAction<Nullable<string>, typeof PLANNER_SESSION_EXCURSION_FILTER_SET_NAME>(PLANNER_SESSION_EXCURSION_FILTER_SET_NAME)
export type PayloadPlannerExcursionFilterName = PayloadAction<Nullable<string>, typeof PLANNER_SESSION_EXCURSION_FILTER_SET_NAME>

export const plannerExcursionFilterSetPrice = createAction<number[], typeof PLANNER_SESSION_EXCURSION_FILTER_SET_PRICE>(PLANNER_SESSION_EXCURSION_FILTER_SET_PRICE)
export type PayloadPlannerExcursionFilterPrice = PayloadAction<number[], typeof PLANNER_SESSION_EXCURSION_FILTER_SET_PRICE>

export const plannerExcursionFilterSetTags = createAction<number[], typeof PLANNER_SESSION_EXCURSION_FILTER_SET_TAGS>(PLANNER_SESSION_EXCURSION_FILTER_SET_TAGS)
export type PayloadPlannerExcursionFilterTags = PayloadAction<number[], typeof PLANNER_SESSION_EXCURSION_FILTER_SET_TAGS>

export const plannerExcursionFilterSetDates = createAction<DateString[], typeof PLANNER_SESSION_EXCURSION_FILTER_SET_DATES>(PLANNER_SESSION_EXCURSION_FILTER_SET_DATES)
export type PayloadPlannerExcursionFilterDates = PayloadAction<DateString[], typeof PLANNER_SESSION_EXCURSION_FILTER_SET_DATES>

export const plannerExcursionFilterReset = createAction<void, typeof PLANNER_SESSION_EXCURSION_FILTER_RESET>(PLANNER_SESSION_EXCURSION_FILTER_RESET)
export type PayloadPlannerExcursionFilterReset = PayloadAction<void, typeof PLANNER_SESSION_EXCURSION_FILTER_RESET>

export const plannerExcursionSetMaxItems = createAction<number, typeof PLANNER_SESSION_EXCURSION_SET_MAX_ITEMS>(PLANNER_SESSION_EXCURSION_SET_MAX_ITEMS)
export type PayloadPlannerExcursionMaxItems = PayloadAction<number, typeof PLANNER_SESSION_EXCURSION_SET_MAX_ITEMS>

export const plannerExcursionSetExcursionDates = createAction<PlannerExcursionDate[], typeof PLANNER_SESSION_EXCURSION_SET_EXCURSION_DATES>(PLANNER_SESSION_EXCURSION_SET_EXCURSION_DATES)
export type PayloadPlannerExcursionSetExcursionDates = PayloadAction<PlannerExcursionDate[], typeof PLANNER_SESSION_EXCURSION_SET_EXCURSION_DATES>

export const plannerExcursionDateUpdate = createAction<PlannerExcursionDate, typeof PLANNER_SESSION_EXCURSION_UPDATE_EXCURSION_DATE>(PLANNER_SESSION_EXCURSION_UPDATE_EXCURSION_DATE)
export type PayloadPlannerExcursionDateUpdate = PayloadAction<PlannerExcursionDate, typeof PLANNER_SESSION_EXCURSION_UPDATE_EXCURSION_DATE>

// CLIENTS

export const plannerSetFirstName = createAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_FIRST_NAME>(PLANNER_SESSION_CLIENT_SET_FIRST_NAME)
export type PayloadPlannerFirstName = PayloadAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_FIRST_NAME>

export const plannerSetLastName = createAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_LAST_NAME>(PLANNER_SESSION_CLIENT_SET_LAST_NAME)
export type PayloadPlannerLastName = PayloadAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_LAST_NAME>

export const plannerSetGender = createAction<KeyOptionValue<GenderItem>, typeof PLANNER_SESSION_CLIENT_SET_GENDER>(PLANNER_SESSION_CLIENT_SET_GENDER)
export type PayloadPlannerGender = PayloadAction<KeyOptionValue<GenderItem>, typeof PLANNER_SESSION_CLIENT_SET_GENDER>

export const plannerSetBirthDay = createAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_BIRTH_DAY>(PLANNER_SESSION_CLIENT_SET_BIRTH_DAY)
export type PayloadPlannerBirthDay = PayloadAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_BIRTH_DAY>

export const plannerSetPassportSerial = createAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_PASSPORT_SERIAL>(PLANNER_SESSION_CLIENT_SET_PASSPORT_SERIAL)
export type PayloadPlannerPassportSerial = PayloadAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_PASSPORT_SERIAL>

export const plannerSetPassportNumber = createAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_PASSPORT_NUMBER>(PLANNER_SESSION_CLIENT_SET_PASSPORT_NUMBER)
export type PayloadPlannerPassportNumber = PayloadAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_PASSPORT_NUMBER>

export const plannerSetPassportExpired = createAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_PASSPORT_EXPIRED>(PLANNER_SESSION_CLIENT_SET_PASSPORT_EXPIRED)
export type PayloadPlannerPassportExpired = PayloadAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_PASSPORT_EXPIRED>

export const plannerSetPassportState = createAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_PASSPORT_STATE>(PLANNER_SESSION_CLIENT_SET_PASSPORT_STATE)
export type PayloadPlannerPassportState = PayloadAction<KeyOptionValue<string>, typeof PLANNER_SESSION_CLIENT_SET_PASSPORT_STATE>
