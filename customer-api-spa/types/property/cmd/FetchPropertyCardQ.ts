import { LangItem } from '../../LangItem'
import { PointItem } from '../../geo/PointItem'
import { DateRange } from '../../DateRange'
import { QueryGroupQ } from '../../bookings/QueryGroup'

export interface FetchPropertyCardQ {
  customerId: string;
  lang: LangItem;
  pointId: string;
  pointCategory: PointItem;
  dates: DateRange;
  group: QueryGroupQ;
}