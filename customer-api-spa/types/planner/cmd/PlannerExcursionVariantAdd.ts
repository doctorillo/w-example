import { Uuid } from '../../basic/Uuid'
import { PlannerExcursionItem } from '../PlannerExcursionItem'

export interface PlannerExcursionVariantAdd {
  plannerId: Uuid;
  clientId: Uuid[];
  plannerPointId: Uuid;
  item: PlannerExcursionItem;
}