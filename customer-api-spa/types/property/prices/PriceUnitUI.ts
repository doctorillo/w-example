import { Amount } from '../../bookings/Amount'
import { PriceUI } from './PriceUI'
import { DateRange } from '../../DateRange'
import { QueryGuest } from '../../bookings/QueryGuest'
import { LangItem } from '../../LangItem'
import { Nullable } from '../../Nullable'
import { Uuid } from '../../basic/Uuid'

export interface PriceUnitUI {
  id: Uuid;
  lang: LangItem;
  customerId: Uuid;
  groupId: Uuid;
  roomPosition: number[];
  guests: QueryGuest[];
  checkInDates: DateRange;
  tariffId: Uuid;
  tariffLabel: string;
  variantId: Uuid;
  boardingId: Uuid;
  boardingLabel: string;
  roomTypeLabel: string;
  roomCategoryLabel: string;
  prices: PriceUI[];
  nights: number;
  price: Amount;
  discount: Nullable<Amount>;
  total: Amount;
  description: Nullable<string>;
  stopSale: boolean;
}