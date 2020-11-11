import { LangItem } from '../../LangItem'
import { PointItem } from '../../geo/PointItem'
import { DateRange } from '../../DateRange'
import { CurrencyItem } from '../../bookings/CurrencyItem'

export interface FetchExcursionCardQ {
  customerId: string;
  lang: LangItem;
  pointId: string;
  pointCategory: PointItem;
  currency: CurrencyItem;
  dates: DateRange;
}