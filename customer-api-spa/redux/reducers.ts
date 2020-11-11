import { combineReducers } from 'redux'
import { reducerEnv } from './modules/env/reducers'
import { reducerRelation } from './modules/relations/reducers'
import { reducerAmenity } from './modules/property-extras/amenities/reducers'
import { reducerFacility } from './modules/property-extras/facilities/reducers'
import { reducerTherapy } from './modules/property-extras/therapies/reducers'
import { reducerIndication } from './modules/property-extras/indications/reducers'
import { reducerPoints } from './modules/points/reducers'
import { reducerProperty } from './modules/property/reducers'
import { reducerTripPlanner } from './modules/trip-planner/reducers'
import { reducerPropertySearch } from './modules/property-search/reducers'
import { reducerExcursionTag } from './modules/excursion-tag/reducers'
import { reducerExcursionSearch } from './modules/excursion-search/reducers'

export default combineReducers({
  sliceEnv : reducerEnv,
  sliceRelation: reducerRelation,
  slicePoints: reducerPoints,
  sliceProperty: reducerProperty,
  sliceTripPlanner: reducerTripPlanner,
  sliceAmenities: reducerAmenity,
  sliceFacilities: reducerFacility,
  sliceTherapies: reducerTherapy,
  sliceIndications: reducerIndication,
  slicePropertySearch: reducerPropertySearch,
  sliceExcursionTag: reducerExcursionTag,
  sliceExcursionSearch: reducerExcursionSearch
})