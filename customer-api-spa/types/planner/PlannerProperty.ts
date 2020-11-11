import { ClientGroupItem } from '../bookings/ClientGroupItem'
import { makeRooms, PlannerRoom } from './PlannerRoom'
import { makePropertyFilterParams, PropertyFilterParams } from './PropertyFilterParams'
import { PlannerClient } from './PlannerClient'
import { v4 as uuid } from 'uuid'
import { LangItem } from '../LangItem'
import { PriceViewMode } from '../PriceViewMode'
import { QueryGroup } from '../bookings/QueryGroup'
import { Uuid } from '../basic/Uuid'

export interface PlannerProperty {
  id: Uuid;
  queryGroup: QueryGroup;
  rooms: PlannerRoom[];
  variantCount: number;
  filter: PropertyFilterParams;
  priceView: PriceViewMode;
  maxItems: number;
}

export interface PlannerPropertyClientPair {
  clients: PlannerClient[];
  property: PlannerProperty;
}

export interface PlannerPropertyLabel {
  category: string;
  label: string;
}

const ra: number[] = [2, 3, 4]
const rb: number[] = [5, 6, 7, 8, 9, 10, 11, 12]

function makeLabelRu (
  clients: PlannerClient[],
  property: PlannerProperty,
): PlannerPropertyLabel {
  const { queryGroup: { category }, rooms } = property
  const roomsCount: number = rooms.length
  const adults = clients.filter(x => !x.age || x.age > 18).length
  const children = clients.filter(x => x.age && x.age < 18).length
  let room = ''
  let a = ''
  let b = ''
  switch (category) {
    case ClientGroupItem.Solo:
      return {
        category: 'Один',
        label: '1 номер, 1 взрослый',
      }

    case ClientGroupItem.Duo:
      return {
        category: 'Парой',
        label: '1 номер, 2 взрослых',
      }

    case ClientGroupItem.Family:
      if (adults === 1) {
        a = ', 1 взрослый'
      } else {
        a = `, ${adults} взрослых`
      }
      if (children === 1) {
        b = ', 1 ребенок'
      } else {
        b = `, ${children} детей`
      }
      if (roomsCount === 1) {
        room = '1 номер'
      }
      if (ra.includes(roomsCount)) {
        room = `${roomsCount} номера`
      }
      if (rb.includes(roomsCount)) {
        room = `${roomsCount} номеров`
      }
      return {
        category: 'С семьей',
        label: `${room}${a}${b}`,
      }

    default:
      if (adults === 1) {
        a = ', 1 взрослый'
      } else {
        a = `, ${adults} взрослых`
      }
      if (roomsCount === 1) {
        room = '1 номер'
      }
      if (ra.includes(roomsCount)) {
        room = `${roomsCount} номера`
      }
      if (rb.includes(roomsCount)) {
        room = `${roomsCount} номеров`
      }
      return {
        category: 'Группой',
        label: `${room}${a}`,
      }
  }
}

function makeLabelEn (
  clients: PlannerClient[],
  property: PlannerProperty,
): PlannerPropertyLabel {
  const { queryGroup: { category }, rooms } = property
  const roomsCount: number = rooms.length
  const adults = clients.filter(x => !x.age || x.age > 18).length
  const children = clients.filter(x => x.age && x.age < 18).length
  let room = ''
  let a = ''
  let b = ''
  switch (category) {
    case ClientGroupItem.Solo:
      return {
        category: 'В одиночку',
        label: '1 номер, 1 взрослый',
      }

    case ClientGroupItem.Duo:
      return {
        category: 'Парой',
        label: '1 номер, 2 взрослых',
      }

    case ClientGroupItem.Family:
      if (adults === 1) {
        a = ', 1 взрослый'
      } else {
        a = `, ${adults} взрослых`
      }
      if (children === 1) {
        b = ', 1 ребенок'
      } else {
        b = `, ${children} детей`
      }
      if (roomsCount === 1) {
        room = '1 номер'
      }
      if (ra.includes(roomsCount)) {
        room = `${roomsCount} номера`
      }
      if (rb.includes(roomsCount)) {
        room = `${roomsCount} номеров`
      }
      return {
        category: 'С семьей',
        label: `${room}${a}${b}`,
      }
    default:
      if (adults === 1) {
        a = ', 1 взрослый'
      } else {
        a = `, ${adults} взрослых`
      }
      if (roomsCount === 1) {
        room = '1 номер'
      }
      if (ra.includes(roomsCount)) {
        room = `${roomsCount} номера`
      }
      if (rb.includes(roomsCount)) {
        room = `${roomsCount} номеров`
      }
      return {
        category: 'Группой',
        label: `${room}${a}`,
      }
  }
}

export function makeLabel (
  clients: PlannerClient[],
  property: PlannerProperty,
  lang: LangItem,
): PlannerPropertyLabel {
  switch (lang) {
    case LangItem.Ru:
      return makeLabelRu(clients, property)
    case LangItem.En:
      return makeLabelEn(clients, property)
    default:
      return makeLabelEn(clients, property)
  }
}

export const makePlannerProperty = (queryGroup: QueryGroup): PlannerPropertyClientPair => {
  const acc = makeRooms(queryGroup)
  const property: PlannerProperty = {
    id: uuid(),
    queryGroup: queryGroup,
    rooms: acc.rooms,
    variantCount: 0,
    filter: makePropertyFilterParams(),
    priceView: PriceViewMode.PerRoom,
    maxItems: 10,
  }
  return {
    clients: acc.clients,
    property,
  }
}
