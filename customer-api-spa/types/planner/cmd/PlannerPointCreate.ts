import { PointOption } from '../../geo/PointOption'
import { DateRange } from '../../DateRange'
import { QueryGroup } from '../../bookings/QueryGroup'
import { Uuid } from '../../basic/Uuid'

export interface PlannerPointCreate {
  plannerId: Uuid;
  point: PointOption;
  dates: DateRange;
  queryGroup: QueryGroup;
}