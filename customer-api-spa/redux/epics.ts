import { combineEpics } from 'redux-observable'
import epicsEnv from './modules/env/epics'
import relationEpic from './modules/relations/epics'
import pointsEpic from './modules/points/epics'
import amenityEpic from './modules/property-extras/amenities/epics'
import facilityEpic from './modules/property-extras/facilities/epics'
import therapyEpic from './modules/property-extras/therapies/epics'
import indicationEpic from './modules/property-extras/indications/epics'
import tripPlannerEpic from './modules/trip-planner/epics'
import searchPropertyEpic from './modules/property-search/epics'
import propertyEpic from './modules/property/epics'
import excursionTagEpic from './modules/excursion-tag/epics'
import searchExcursionEpic from './modules/excursion-search/epics'

export default combineEpics(
  epicsEnv,
  relationEpic,
  pointsEpic,
  searchPropertyEpic,
  amenityEpic,
  facilityEpic,
  therapyEpic,
  indicationEpic,
  propertyEpic,
  tripPlannerEpic,
  excursionTagEpic,
  searchExcursionEpic
)
