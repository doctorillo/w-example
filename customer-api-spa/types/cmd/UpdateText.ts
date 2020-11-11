import { Nullable } from '../Nullable'

export interface Update<A, B> {
  id: A;
  value: Nullable<B>;
}