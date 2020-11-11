import { Uuid } from '../../basic/Uuid'

export interface PlannerAccommodationVariantSelect {
  plannerId: Uuid;
  plannerPointId: Uuid;
  plannerRoomId: Uuid;
  itemId: Uuid;
}