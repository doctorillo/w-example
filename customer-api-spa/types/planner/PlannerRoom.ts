import { makeClient, makeClients, PlannerClient, PlannerClientId } from './PlannerClient'
import { v4 as uuid } from 'uuid'
import { PlannerAccommodationItem, PlannerAccommodationItemId } from './PlannerAccommodationItem'
import { Nullable } from '../Nullable'
import { QueryGroup } from '../bookings/QueryGroup'
import { QueryRoom } from '../bookings/QueryRoom'

export type PlannerRoomId = string

export interface PlannerRoom {
  id: PlannerRoomId;
  clients: PlannerClientId[];
  variants: PlannerAccommodationItem[];
  selected: Nullable<PlannerAccommodationItemId>;
  position: number;
}

export interface PlannerRoomClientPair {
  clients: PlannerClient[];
  rooms: PlannerRoom[];
}

export interface PlannerRoomCreatorAcc {
  clients: PlannerClient[];
  rooms: PlannerRoom[];
  lastRoomPosition: number;
  lastClientPosition: number;
}

export function makeSolo(
  lastRoomOrder = -1,
  lastClientOrder = -1
): PlannerRoomClientPair {
  const clients: PlannerClient[] = [makeClient(lastClientOrder + 1, null)]
  const rooms: PlannerRoom[] = [
    {
      id: uuid(),
      clients: clients.map(x => x.id),
      variants: [],
      selected: null,
      position: lastRoomOrder + 1,
    },
  ]
  return {
    clients,
    rooms,
  }
}
export function makeDuo(
  lastRoomOrder = -1,
  lastClientOrder = -1
): PlannerRoomClientPair {
  const clients: PlannerClient[] = [
    makeClient(lastClientOrder + 1, null),
    makeClient(lastClientOrder + 2, null),
  ]
  const rooms: PlannerRoom[] = [
    {
      id: uuid(),
      clients: clients.map(x => x.id),
      variants: [],
      selected: null,
      position: lastRoomOrder + 1,
    },
  ]
  return {
    clients,
    rooms,
  }
}
export function makeFamily(
  lastRoomOrder = -1,
  lastClientOrder = -1
): PlannerRoomClientPair {
  const clients: PlannerClient[] = [
    makeClient(lastClientOrder + 1, null),
    makeClient(lastClientOrder + 2, null),
    makeClient(lastClientOrder + 3, 1),
  ]
  const rooms: PlannerRoom[] = [
    {
      id: uuid(),
      clients: clients.map(x => x.id),
      variants: [],
      selected: null,
      position: lastRoomOrder + 1,
    },
  ]
  return {
    clients,
    rooms,
  }
}

export const makeRoom = (queryRoom: QueryRoom): PlannerRoomClientPair => {
  const clients = makeClients(queryRoom.guests)
  const rooms = [{
    id: uuid(),
    clients: clients.map(x => x.id),
    variants: [],
    selected: null,
    position: queryRoom.position
  }]
  return { clients, rooms }
}

export const makeRooms = (queryGroup: QueryGroup): PlannerRoomClientPair => {
  const acc: PlannerRoomClientPair = {clients: [], rooms: []}
  const maker = queryGroup.rooms.reduce((acc: PlannerRoomClientPair, x: QueryRoom) => {
    const {clients, rooms} = makeRoom(x)
    return {
      clients: [...acc.clients, ...clients],
      rooms: [...acc.rooms, ...rooms]
    }
  }, acc)
  return {
    clients: maker.clients,
    rooms: maker.rooms
  }
}

export function variantExist(xs: PlannerRoom[]): boolean {
  return xs.reduce((acc: boolean, x: PlannerRoom) => {
    if (acc) {
      return acc
    } else {
      return x.variants.length > 0
    }
  }, false)
}
/*
export function transform(clients: PlannerClient[], property: PlannerProperty) {

}*/

/*export function plannerRoomsToQuery(zs: PlannerClient[], xs: PlannerRoom[], category: ClientGroupItem): QueryGroup {
  const cr = xs.map(x => {
    const g: QueryGuest[] = x.clients.map((itemId: string, position: number) => ({
      itemId,
      position
    }))
  })
  return ({
    itemId: uuid(),
    category,
    propertyRooms: [],
  })
}*/
