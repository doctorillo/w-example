import { ClientGroupItem } from '../bookings/ClientGroupItem'
import { LangItem } from '../LangItem'

export interface PersonGroup {
  category: ClientGroupItem;
  rooms: number;
  adults: number;
  children: number[];
}

export interface GroupLabel {
  category: string;
  label: string;
}

export const soloInit: () => PersonGroup = () => ({
  category: ClientGroupItem.Solo,
  rooms: 1,
  adults: 1,
  children: [],
})

export const duoInit: () => PersonGroup = () => ({
  category: ClientGroupItem.Duo,
  rooms: 1,
  adults: 2,
  children: [],
})


const ra = [2, 3, 4]
const rb = [5, 6, 7, 8, 9, 10, 11, 12]

export function groupLabel(group: PersonGroup, lang: LangItem): GroupLabel {
  if (lang === LangItem.Ru) {
    if (group.category === ClientGroupItem.Solo) {
      return {
        category: 'В одиночку',
        label: '1 номер, 1 взрослый',
      }
    }
    if (group.category === ClientGroupItem.Duo) {
      return {
        category: 'Парой',
        label: '1 номер, 2 взрослых',
      }
    }
    if (group.category === ClientGroupItem.Family) {
      let room = ''
      if (group.rooms === 1) {
        room = '1 номер'
      }
      if (ra.includes(group.rooms)) {
        room = `${group.rooms} номера`
      }
      if (rb.includes(group.rooms)) {
        room = `${group.rooms} номеров`
      }
      const adult = `${group.adults} взрослых`
      const children = group.children.length === 0 ? '' : group.children.length === 1 ? `, ${group.children.length} ребенок` : `, ${group.children.length} детей`
      if (group) {
        return {
          category: 'С семьей',
          label: `${room}, ${adult}${children}`,
        }
      } else {
        return {
          category: 'С семьей',
          label: '',
        }
      }
    }
  }
  if (group.category === ClientGroupItem.Group) {
    let room = ''
    if (group.rooms === 1) {
      room = '1 номер'
    }
    if (ra.includes(group.rooms)) {
      room = `${group.rooms} номера`
    }
    if (rb.includes(group.rooms)) {
      room = `${group.rooms} номеров`
    }
    const adult = group.adults === 1 ? '1 взрослый' : `${group.adults} взрослых`
    if (group) {
      return {
        category: 'Группой',
        label: `${room}, ${adult}`,
      }
    } else {
      return {
        category: 'Группой',
        label: '',
      }
    }
  }
  return {
    category: '',
    label: '',
  }
}
