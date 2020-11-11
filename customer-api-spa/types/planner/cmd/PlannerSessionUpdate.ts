import { QueryGroup } from '../../bookings/QueryGroup'
import { PointOption } from '../../geo/PointOption'
import { DateRange } from '../../DateRange'
import { Uuid } from '../../basic/Uuid'
import { PlannerSessionId } from '../PlannerSession'

export interface PlannerSessionUpdate {
  plannerId: PlannerSessionId;
  solverId: Uuid;
  customerId: Uuid;
  customerName: string;
  point: PointOption;
  dates: DateRange;
  queryGroup: QueryGroup;
}