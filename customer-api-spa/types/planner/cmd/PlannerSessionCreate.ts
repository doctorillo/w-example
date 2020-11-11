import { QueryGroup } from '../../bookings/QueryGroup'
import { PointOption } from '../../geo/PointOption'
import { DateRange } from '../../DateRange'

export interface PlannerSessionCreate {
  solverId: string;
  customerId: string;
  customerName: string;
  point: PointOption;
  dates: DateRange;
  queryGroup: QueryGroup;
  withExcursion: boolean;
  withTransfer: boolean;
}