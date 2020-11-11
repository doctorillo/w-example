import { PointOption } from '../../geo/PointOption'
import { DateRange } from '../../DateRange'

export interface AddLocation {
  id: string;
  city: PointOption;
  dates: DateRange;
}