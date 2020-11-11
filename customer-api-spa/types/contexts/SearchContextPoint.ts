import { ContextItem } from '../basic/ContextItem'
import { DateRange } from '../DateRange'
import { PointOption } from '../geo/PointOption'
import { QueryGroup } from '../bookings/QueryGroup'
import { Nullable } from '../Nullable'

export interface SearchContextPoint {
  id: string;
  ctx: ContextItem;
  customerId: Nullable<string>;
  point: Nullable<PointOption>;
  dates: Nullable<DateRange>;
  group: Nullable<QueryGroup>;
}