import { Uuid } from '../basic/Uuid'
import { PlannerExcursionItem } from './PlannerExcursionItem'
import { DateString } from '../basic/DateString'

export interface PlannerExcursionClientItem {
  clientId: Uuid;
  date: DateString;
  variants: PlannerExcursionItem[];
  selected: Uuid[];
}