import { PropertyCardUI } from '../bookings/PropertyCardUI'
import { BoardingUI } from '../bookings/BoardingUI'
import { PropertyFilterParams } from './PropertyFilterParams'
import { Nullable } from '../Nullable'

function byStop (items: PropertyCardUI[], stop: boolean): PropertyCardUI[] {
  return stop
    ? items
    : items.filter(
      (x: PropertyCardUI) => x.bestPrice && !x.bestPrice.stopSale,
    )
}

function byPrice (items: PropertyCardUI[], price: number[]): PropertyCardUI[] {
  return price.length !== 2
    ? items
    : items.filter(
      (x: PropertyCardUI) => x.bestPrice && price[0] <= x.bestPrice.total.value && price[1] >= x.bestPrice.total.value,
    )
}

function byName (
  items: PropertyCardUI[],
  name: Nullable<string>,
): PropertyCardUI[] {
  return !name || name === ''
    ? items
    : items.filter((x: PropertyCardUI) =>
      x.name.toLowerCase().includes(name.toLowerCase(), 0),
    )
}

function byStar (items: PropertyCardUI[], stars: number[]): PropertyCardUI[] {
  return stars.length !== 2
    ? items
    : items.filter(
      (x: PropertyCardUI) => stars[0] <= x.star && stars[1] >= x.star,
    )
}

function byBoarding (
  items: PropertyCardUI[],
  boardings: string[],
): PropertyCardUI[] {
  if (boardings.length === 0) {
    return items
  }
  return items.filter((x: PropertyCardUI) => {
    const intersect = x.boardings.filter((z: BoardingUI) => boardings.includes(z.id))
    return intersect.length > 0
  })
}

function byAmenity (
  items: PropertyCardUI[],
  amenities: string[],
): PropertyCardUI[] {
  if (amenities.length === 0) {
    return items
  }
  return items.filter((x: PropertyCardUI) => {
    const intersect = x.amenities.filter((z: string) => amenities.includes(z))
    return intersect.length === amenities.length
  })
}

function byFacility (
  items: PropertyCardUI[],
  facilities: string[],
): PropertyCardUI[] {
  if (facilities.length === 0) {
    return items
  }
  return items.filter(
    (x: PropertyCardUI) => {
      const intersect = x.facilities.filter((z: string) => facilities.includes(z))
      return intersect.length === facilities.length
    },
  )
}

function byIndication (
  items: PropertyCardUI[],
  indications: string[],
): PropertyCardUI[] {
  if (indications.length === 0) {
    return items
  }
  return items.filter(
    (x: PropertyCardUI) => {
      const intersect = x.indications.filter((z: string) => indications.includes(z))
      return intersect.length === indications.length
    },
  )
}

function byTherapy (
  items: PropertyCardUI[],
  therapies: string[],
): PropertyCardUI[] {
  if (therapies.length === 0) {
    return items
  }
  return items.filter(
    (x: PropertyCardUI) => {
      const intersect = x.therapies.filter((z: string) => therapies.includes(z))
      return intersect.length === therapies.length
    },
  )
}

export function filterProperty (items: PropertyCardUI[], params: PropertyFilterParams): PropertyCardUI[] {
  const {
    name,
    stars,
    price,
    boardings,
    amenities,
    facilities,
    indications,
    therapies,
    viewStop,
  } = params
  const xs = byStop(items, viewStop)
  const a = byName(xs, name)
  const b = byStar(a, stars)
  const c = byAmenity(b, amenities)
  const d = byFacility(c, facilities)
  const e = byIndication(d, indications)
  const f = byTherapy(e, therapies)
  const g = byPrice(f, price)
  return  byBoarding(g, boardings)
}