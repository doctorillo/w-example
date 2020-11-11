import { ClientGroupItem } from '../../bookings/ClientGroupItem'

export interface SessionPointPropertyGroupUpdate {
  sessionId: string;
  sessionPointId: string;
  sessionPropertyId: string;
  group: ClientGroupItem;
}