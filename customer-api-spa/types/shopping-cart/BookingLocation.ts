import { DateRange } from '../DateRange'
import { PlannerAccommodationItem } from '../planner/PlannerAccommodationItem'
import { PointOption } from '../geo/PointOption'

export interface BookingLocation {
  id: string;
  city: PointOption;
  dates: DateRange;
  accommodations: PlannerAccommodationItem[];
}