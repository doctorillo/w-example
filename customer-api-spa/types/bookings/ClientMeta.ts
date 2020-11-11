import { GenderItem } from '../parties/GenderItem'
import { Nullable } from '../Nullable'

export type ClientMetaId = string

export interface ClientMeta {
  id: ClientMetaId;
  firstName: Nullable<string>;
  lastName: Nullable<string>;
  gender: Nullable<GenderItem>;
  birthDay: Nullable<string>;
  passport: PassportMeta;
}

export interface PassportMeta {
  serial: Nullable<string>;
  number: Nullable<string>;
  state: Nullable<string>;
  expiredAt: Nullable<string>;
}

export function makeClientMeta(
  id: string,
  firstName: Nullable<string>,
  lastName: Nullable<string>,
  gender: Nullable<GenderItem>,
  birthDay: Nullable<string>,
  passport: PassportMeta
): ClientMeta {
  return {
    id,
    firstName,
    lastName,
    gender,
    birthDay,
    passport,
  }
}

export function makePassportMeta(
  serial: Nullable<string>,
  number: Nullable<string>,
  state: Nullable<string>,
  expiredAt: Nullable<string>
): PassportMeta {
  return {
    serial,
    number,
    state,
    expiredAt,
  }
}
