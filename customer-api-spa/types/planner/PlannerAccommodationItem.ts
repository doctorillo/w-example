import { Amount } from '../bookings/Amount'
import { DateRange, formatLocalDateTime } from '../DateRange'
import { v4 as uuid } from 'uuid'
import { Uuid } from '../basic/Uuid'
import { Nullable } from '../Nullable'

export type PlannerAccommodationItemId = string

export interface PlannerAccommodationItem {
  id: PlannerAccommodationItemId;
  created: string;
  updated: string;
  dates: DateRange;
  propertyId: Uuid;
  propertyName: string;
  propertyStar: number;
  roomPriceUnitId: Uuid;
  roomType: string;
  roomCategory: string;
  boarding: string;
  tariffId: Uuid;
  tariff: string;
  groupId: Uuid;
  nights: number;
  price: Amount;
  discount: Nullable<Amount>;
  markAsBest: boolean;
  markAsDelete: boolean;
}

export function makeItem(
  dates: DateRange,
  propertyId: Uuid,
  propertyName: string,
  propertyStar: number,
  roomPriceUnitId: Uuid,
  roomType: string,
  roomCategory: string,
  boarding: string,
  tariffId: Uuid,
  tariff: string,
  groupId: string,
  nights: number,
  amount: Amount,
  discount: Nullable<Amount>
): PlannerAccommodationItem {
  return {
    id: uuid(),
    created: formatLocalDateTime(new Date()),
    updated: formatLocalDateTime(new Date()),
    markAsBest: false,
    markAsDelete: false,
    dates,
    propertyId,
    propertyName,
    propertyStar,
    roomPriceUnitId,
    roomType,
    roomCategory,
    boarding,
    tariffId,
    tariff,
    groupId,
    nights,
    price: amount,
    discount
  }
}
