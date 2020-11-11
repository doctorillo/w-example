import { Uuid } from '../basic/Uuid'
import { DateString } from '../basic/DateString'

export interface PlannerExcursionDate {
  excursionOfferId: Uuid;
  selected: DateString;
}