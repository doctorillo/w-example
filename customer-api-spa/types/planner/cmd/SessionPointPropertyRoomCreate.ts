import { QueryRoom } from '../../bookings/QueryRoom'

export interface SessionPointPropertyRoomCreate {
  plannerId: string;
  plannerPointId: string;
  plannerPointPropertyId: string;
  queryRoom: QueryRoom;
}