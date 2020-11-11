import { Uuid } from '../../basic/Uuid'

export interface PlannerExcursionVariantRemove {
  plannerId: Uuid;
  plannerPointId: Uuid;
  plannerClientId: Uuid[];
  itemId: Uuid;
}