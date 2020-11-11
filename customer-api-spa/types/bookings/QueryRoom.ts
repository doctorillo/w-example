import { QueryGuest } from './QueryGuest'
import { guestInit } from './QueryGuest'

export interface QueryRoom {
  guests: QueryGuest[];
  position: number;
}

export function roomInit(position: number, adults: number, children = 0): QueryRoom {
  const guests: QueryGuest[] = []
  for (let i = 0; i < adults; i++){
    guests.push(guestInit(null, null, i))
  }
  for (let i = guests.length; i < guests.length + children; i++){
    guests.push(guestInit(1, null, i))
  }
  return {
    guests,
    position,
  }
}

export function roomAdults(room: QueryRoom): number {
  return room.guests.filter((z: QueryGuest) => !z.age || z.age > 18).length
}

export function roomChildren(room: QueryRoom): number {
  return room.guests.filter((z: QueryGuest) => z.age && z.age < 18).length
}

