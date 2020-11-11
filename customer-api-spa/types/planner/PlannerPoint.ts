import { PointOption } from '../geo/PointOption'
import { DateRange } from '../DateRange'
import { makePlannerProperty, PlannerProperty } from './PlannerProperty'
import { PlannerClient } from './PlannerClient'
import { v4 as uuid } from 'uuid'
import { Nullable } from '../Nullable'
import { QueryGroup } from '../bookings/QueryGroup'
import { makePlannerExcursion, PlannerExcursion } from './PlannerExcursion'

export type PlannerPointId = string

export interface PlannerPoint {
  id: PlannerPointId;
  point: PointOption;
  dates: DateRange;
  property: Nullable<PlannerProperty>;
  transfer: Nullable<string>;
  excursion: Nullable<PlannerExcursion>;
  variantCount: number;
}

export interface PlannerPointClientPair {
  clients: PlannerClient[];
  plannerPoint: PlannerPoint;
}

/*function dateRange(): DateRange {
  return {
    from: format(addDays(new Date(), 7), 'yyyy-MM-dd'),
    to: format(addDays(new Date(), 14), 'yyyy-MM-dd'),
  }
}*/

/*function fromPlannerRooms(xs: PlannerRoom[]): PlannerRoom[] {
  return xs.map((x: PlannerRoom) => ({ ...x, itemId: uuid(), excursionClientItems: [], excursionSelectedDates: null }))
}*/

export const makePlannerPoint = (
  point: PointOption,
  dates: DateRange,
  queryGroup: QueryGroup,
): PlannerPointClientPair => {
  const { clients, property } = makePlannerProperty(queryGroup)
  const excursion = makePlannerExcursion(queryGroup)
  const plannerPoint: PlannerPoint = {
    id: uuid(),
    point: point,
    dates: dates,
    property,
    transfer: null,
    excursion,
    variantCount: 0,
  }
  return {
    clients,
    plannerPoint,
  }
}

/*export function makePlannerPointFrom(
  point: PointOption,
  from: PlannerPoint,
): Nullable<PlannerPoint> {
  if (!from.property) {
    return null
  }
  const propertyRooms = fromPlannerRooms(from.property.propertyRooms)
  const property: PlannerProperty = {
    itemId: uuid(),
    category: from.property.category,
    propertyRooms: propertyRooms,
    filter: makePropertyFilterParams(),
    propertyPriceView: from.property.propertyPriceView,
    propertyMaxItems: 10,
  }
  return {
    itemId: uuid(),
    point: point,
    excursionSelectedDates: from.excursionSelectedDates,
    property,
  }
}*/

/*export function makePlannerPointFromDate(
  excursionSelectedDates: DateRange,
  from: PlannerPoint,
): Nullable<PlannerPoint> {
  if (!from.property) {
    return null
  }
  const propertyRooms = fromPlannerRooms(from.property.propertyRooms)
  const property: PlannerProperty = {
    itemId: uuid(),
    category: from.property.category,
    propertyRooms: propertyRooms,
    filter: makePropertyFilterParams(),
    propertyPriceView: from.property.propertyPriceView,
    propertyMaxItems: 10,
  }
  return ({ ...from, excursionSelectedDates, property })
}*/
