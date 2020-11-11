import { Uuid } from '../../basic/Uuid'

export interface PlannerAccommodationVariantRemove {
  plannerId: Uuid;
  plannerPointId: Uuid;
  plannerRoomId: Uuid;
  id: Uuid;
}