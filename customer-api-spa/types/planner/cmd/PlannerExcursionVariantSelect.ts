import { Uuid } from '../../basic/Uuid'
import { PlannerExcursionItemId } from '../PlannerExcursionItem'
import { ActionSelect } from '../../ActionSelect'

export interface PlannerExcursionVariantSelect {
  plannerId: Uuid;
  plannerPointId: Uuid;
  itemId: PlannerExcursionItemId;
  actionType: ActionSelect;
}