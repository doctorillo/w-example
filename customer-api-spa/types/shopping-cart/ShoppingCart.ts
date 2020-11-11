import { BookingLocation } from './BookingLocation'
import { QueryGroup } from '../bookings/QueryGroup'
import { QueryRoom } from '../bookings/QueryRoom'
import { QueryGuest } from '../bookings/QueryGuest'
import { Client } from './Client'
import { v4 as uuid } from 'uuid'
import { Group } from './Group'
import { Nullable } from '../Nullable'

export interface ShoppingCart {
  id: string;
  customerId: string;
  bookingNumber: string;
  groups: Group[];
  locations: BookingLocation[];
  notes: Nullable<string>;
}

export function makeCart(customerId: string, group: QueryGroup): ShoppingCart {
  const cxs: Group[] = group.rooms.map((x: QueryRoom) => {
    const c: Client[] = x.guests.map((z: QueryGuest) => ({
      id: uuid(),
      firstName: null,
      lastName: null,
      birthDay: null,
      passport: null,
      age: z.age,
      position: z.position
    }))
    return {
      id: uuid(),
      clients: c,
      position: x.position,
    }
  })
  return {
    id: uuid(),
    customerId: customerId,
    bookingNumber: '',
    groups: cxs,
    locations: [],
    notes: null,
  }
}