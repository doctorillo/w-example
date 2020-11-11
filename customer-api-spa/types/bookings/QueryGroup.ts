import { ClientGroupItem } from './ClientGroupItem'
import { QueryRoom, roomAdults, roomChildren, roomInit } from './QueryRoom'
import { LangItem } from '../LangItem'
import { GroupLabel } from '../parties/PersonGroup'
import { v4 as uuid } from 'uuid'
import { Uuid } from '../basic/Uuid'

export interface QueryGroup {
  id: Uuid;
  category: ClientGroupItem;
  rooms: QueryRoom[];
}

export interface QueryGroupQ {
  category: ClientGroupItem;
  rooms: QueryRoom[];
}

export function adultCount (group: QueryGroup): number {
  return group.rooms.reduce((acc, x: QueryRoom) => acc + roomAdults(x), 0)
}

export function childrenCount (group: QueryGroup): number {
  return group.rooms.reduce((acc, x: QueryRoom) => acc + roomChildren(x), 0)
}

export function soloGroupInit (): QueryGroup {
  return ({
    id: uuid(),
    category: ClientGroupItem.Solo,
    rooms: [roomInit(0, 1)],
  })
}

export function duoGroupInit (): QueryGroup {
  return ({
    id: uuid(),
    category: ClientGroupItem.Duo,
    rooms: [roomInit(0, 2)],
  })
}

const ra = [2, 3, 4]

export function groupLabel (group: QueryGroup, lang: LangItem): GroupLabel {
  const adults = adultCount(group)
  const children = childrenCount(group)
  const roomCount = group.rooms.length
  const roomLabel = roomCount === 1 ? '1 номер' : ra.includes(roomCount) ? `${roomCount} номера` : `${roomCount} номеров`
  const adultLabel = adults === 1 ? '1 взрослый' : `${adults} взрослых`
  const childrenLabel = children === 0 ? '' : children === 1 ? `, ${children} ребенок` : `, ${children} детей`
  if (lang === LangItem.Ru) {
    if (group.category === ClientGroupItem.Solo) {
      return {
        category: 'В одиночку',
        label: `${roomLabel}, ${adultLabel}`,
      }
    }
    if (group.category === ClientGroupItem.Duo) {
      return {
        category: 'Парой',
        label: `${roomLabel}, ${adultLabel}`,
      }
    }
    if (group.category === ClientGroupItem.Family) {
      return {
        category: 'С семьей',
        label: `${roomLabel}, ${adultLabel}${childrenLabel}`,
      }
    }
  }
  if (group.category === ClientGroupItem.Group) {
    return {
      category: 'Группой',
      label: `${roomLabel}, ${adultLabel}${childrenLabel}`,
    }
  }
  return {
    category: 'No category.',
    label: `${roomLabel}, ${adultLabel}${childrenLabel}`,
  }
}

export interface QueryRoomRemoveAcc {
  queryGroup: QueryGroup;
  roomMapping: Map<number, number>;
  guestMapping: Map<number, number>;
}

export const removeRoom = (queryGroup: QueryGroup, roomPosition: number): QueryGroup => {
  const acc: QueryRoom[] = []
  const rooms = queryGroup.rooms.filter(x => x.position !== roomPosition)
    rooms.reduce((a, x, idx) => {
    if (x.position !== idx){

    }
    return [...a, x]
  }, acc)
  return { ...queryGroup, rooms }
}