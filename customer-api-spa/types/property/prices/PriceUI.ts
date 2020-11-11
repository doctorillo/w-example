import { Amount } from '../../bookings/Amount'
import { DateRange } from '../../DateRange'

export interface PriceUI {
  id: string;
  priceDateId: string;
  dates: DateRange;
  nights: number;
  price: Amount;
  total: Amount;
}