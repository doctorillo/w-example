import { DateRange } from '../../DateRange'

export interface PlannerSessionDatesUpdate {
  sessionId: string;
  sessionPointId: string;
  dates: DateRange;
}