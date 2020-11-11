import { PointOption } from '../../geo/PointOption'

export interface SessionPointUpdate {
  sessionId: string;
  sessionPointId: string;
  point: PointOption;
}