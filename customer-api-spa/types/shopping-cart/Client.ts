import { Passport } from './Passport'
import { Nullable } from '../Nullable'

export interface Client {
  id: string;
  firstName: Nullable<string>;
  lastName: Nullable<string>;
  birthDay: Nullable<Date>;
  passport: Nullable<Passport>;
  age: Nullable<number>;
  position: number;
}