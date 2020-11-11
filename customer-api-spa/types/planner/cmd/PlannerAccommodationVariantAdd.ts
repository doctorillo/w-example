import { PlannerAccommodationItem } from '../PlannerAccommodationItem'
import { Uuid } from '../../basic/Uuid'

export interface PlannerAccommodationVariantAdd {
  plannerId: Uuid;
  plannerPointId: Uuid;
  plannerPropertyId: Uuid;
  plannerRoomId: Uuid;
  room: PlannerAccommodationItem;
}