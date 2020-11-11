import { Nullable } from './Nullable'

export interface KeyOptionValue<A> {
  id: string;
  value: Nullable<A>;
}