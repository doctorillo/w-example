import { LangItem } from '../LangItem'
import { v4 as uuid } from 'uuid'
import { ClientMeta, makeClientMeta, makePassportMeta } from '../bookings/ClientMeta'
import { Nullable } from '../Nullable'
import { QueryGuest } from '../bookings/QueryGuest'

export type PlannerClientId = string

export interface PlannerClient {
  id: PlannerClientId;
  age: Nullable<number>;
  meta: ClientMeta;
  position: number;
  tag(lang: LangItem): string;
}

class PlannerClientImpl implements PlannerClient {
  id: string;
  age: Nullable<number>;
  meta: ClientMeta;
  position: number;
  constructor(id: string, age: Nullable<number>, meta: ClientMeta, position: number) {
    this.id = id
    this.age = age
    this.meta = meta
    this.position = position
  }
  tag(lang: LangItem): string {
    const ageChild = 12
    let ageString: string =
      !this.age || this.age > ageChild ? '' : ` ребенок ${this.age} лет`
    switch (lang) {
      case LangItem.Ru:
        ageString =
          !this.age || this.age > ageChild ? '' : ` ребенок ${this.age} лет`
        break
      default:
        ageString =
          !this.age || this.age > ageChild ? '' : ` child ${this.age} year`
        break
    }
    switch (lang) {
      case LangItem.Ru:
        return `${this.position} турист - ${ageString}`
      default:
        return `${this.position} tourist - ${ageString}`
    }
  }
}

export function makeClient(position: number, age: Nullable<number>): PlannerClient {
  return new PlannerClientImpl(uuid(), age, makeClientMeta(
    uuid(),
    null,
    null,
    null,
    null,
    makePassportMeta(null, null, null, null)
  ), position)
}

export const makeClients = (xs: QueryGuest[]): PlannerClient[] => xs.map((x: QueryGuest) => makeClient(x.position, x.age))
