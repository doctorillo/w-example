import { Nullable } from '../../Nullable'

export interface SessionPointPropertyClientCreate {
  plannerId: string;
  plannerPointId: string;
  plannerPropertyId: string;
  plannerRoomId: string;
  age: Nullable<number>;
}