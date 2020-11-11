import { PlannerClient } from './PlannerClient'
import { PlannerPoint, PlannerPointId } from './PlannerPoint'
import { Nullable } from '../Nullable'
import { QueryGroup } from '../bookings/QueryGroup'
import { PlannerRoom } from './PlannerRoom'
import { STATE_TRIP_PLANNER } from '../../redux/modules/trip-planner/reducers'
import { Uuid } from '../basic/Uuid'
import { QueryClientGroup } from '../bookings/QueryClientGroup'
import { PropertyFilterParams } from './PropertyFilterParams'
import { PriceViewMode } from '../PriceViewMode'
import { ExcursionFilterParams } from './ExcursionFilterParams'
import { PlannerExcursionDate } from './PlannerExcursionDates'
import { DateString } from '../basic/DateString'
import { PlannerExcursionVariantDate, sortByDate } from './PlannerExcursionVariantDate'
import { PlannerExcursionItemId } from './PlannerExcursionItem'

export type PlannerSessionId = string

export interface PlannerSession {
  id: PlannerSessionId;
  created: DateString;
  updated: DateString;
  solverId: Uuid;
  customerId: Uuid;
  customerName: string;
  identCode: string;
  clients: PlannerClient[];
  points: PlannerPoint[];
  activePoint: Nullable<PlannerPointId>;
  title: Nullable<string>;
  notes: Nullable<string>;
  deleted: boolean;
  bookingStep: number;
}

export const plannerSelectedAsk = (state: STATE_TRIP_PLANNER): Nullable<PlannerSession> => {
  const selected: Nullable<Uuid> = state.extraEnv?.selected || null
  return state.internal
    .find(x => x.id === selected) || null
}

/*export const makeSession = (solverId: string,
                            customerId: string,
                            customerName: string,
                            clients: PlannerClient[],
                            point: PlannerPoint,
                            points: PlannerPoint[]): PlannerSession => ({
  id: uuid(),
  created: formatISO(new Date(), "yyyy-MM-dd'T'HH:mm:ss"),
  updated: formatISO(new Date(), "yyyy-MM-dd'T'HH:mm:ss"),
  solverId,
  customerId,
  customerName,
  identCode: Math.random().toString(36).substring(7),
  clients,
  activePoint: point,
  points,
  title: null,
  notes: null,
  deleted: false
})*/

export interface PlannerBasicSessionAsk {
  session: PlannerSession;
  clients: PlannerClient[];
  point: PlannerPoint;
  points: PlannerPoint[];
  bookingStep: number;
}

export const plannerBasicSessionAsk = (session: Nullable<PlannerSession>): Nullable<PlannerBasicSessionAsk> => {
  if (!session || !session.activePoint) {
    return null
  }
  const point = session.points.find(x => x.id === session.activePoint)
  if (!point) {
    return null
  }
  const points = session.points.filter(x => x.id !== point.id)
  const clients = [...session.clients]
  return ({
    session,
    clients,
    point,
    points,
    bookingStep: session.bookingStep,
  })
}

export interface PlannerPropertySessionAsk {
  propertyId: Uuid;
  propertyGroup: QueryGroup;
  propertyFilter: PropertyFilterParams;
  propertyRooms: PlannerRoom[];
  propertyVariantCount: number;
  propertyPriceView: PriceViewMode;
  propertyMaxItems: number;
}

export const plannerPropertySessionAsk = (session: Nullable<PlannerSession>): Nullable<PlannerPropertySessionAsk> => {
  if (!session || !session.activePoint) {
    return null
  }
  const property = session.points.find(x => x.id === session.activePoint)?.property
  const queryGroup = property?.queryGroup
  if (!property || !queryGroup) {
    return null
  }
  return ({
    propertyId: property.id,
    propertyGroup: queryGroup,
    propertyFilter: property.filter,
    propertyRooms: property.rooms,
    propertyVariantCount: property.variantCount,
    propertyPriceView: property.priceView,
    propertyMaxItems: property.maxItems
  })
}

export interface PlannerExcursionSessionAsk {
  excursionId: Uuid;
  excursionGroup: QueryClientGroup;
  excursionFilter: ExcursionFilterParams;
  excursionClientItems: PlannerExcursionVariantDate[];
  excursionSelected: PlannerExcursionItemId[];
  excursionVariantCount: number;
  excursionDates: DateString[];
  excursionSelectedDates: PlannerExcursionDate[];
  excursionMaxItems: number;
}

export const plannerExcursionSessionAsk = (session: Nullable<PlannerSession>): Nullable<PlannerExcursionSessionAsk> => {
  if (!session || !session.activePoint) {
    return null
  }
  const excursion = session.points.find(x => x.id === session.activePoint)?.excursion
  const queryGroup = excursion?.queryGroup
  if (!excursion || !queryGroup) {
    return null
  }
  const variantCount = excursion.items.reduce((a, x) => a + x.variantCount, 0)
  return ({
    excursionId: excursion.id,
    excursionGroup: queryGroup,
    excursionFilter: excursion.filter,
    excursionClientItems: excursion.items.slice().sort(sortByDate),
    excursionSelected: excursion.selected,
    excursionVariantCount: variantCount,
    excursionMaxItems: excursion.maxItems,
    excursionDates: excursion.dates,
    excursionSelectedDates: excursion.excursionDates,
  })
}