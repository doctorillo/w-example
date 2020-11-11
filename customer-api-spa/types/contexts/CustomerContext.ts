import { DateRange } from '../DateRange'
import { PointOption } from '../geo/PointOption'

export interface CustomerContext {
  id: string;
  name: string;
  point: PointOption;
  dates: DateRange;
}