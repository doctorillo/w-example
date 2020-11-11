import { Nullable } from '../Nullable'

export type QueryGuest = {
  age: Nullable<number>;
  boarding: Nullable<string>;
  position: number;
}

export const guestInit = (age: Nullable<number>, boarding: Nullable<string>, position: number): QueryGuest => ({
  age,
  boarding,
  position,
})

export const makeGuest = (position: number): QueryGuest => guestInit(null, null, position)

export const AdultDefaultAge = 25

export const isAdult = (x: QueryGuest): boolean => !x.age || x.age >= AdultDefaultAge

export const isChild = (x: QueryGuest): boolean => !!x.age && x.age <= 14