import { ExcursionFilterParams } from './ExcursionFilterParams'
import { Nullable } from '../Nullable'
import { ExcursionCardUI } from '../bookings/ExcursionCardUI'
import { ExcursionTagItem } from '../bookings/ExcursionTagItem'
import { DateString } from '../basic/DateString'
import { PlannerExcursionDate } from './PlannerExcursionDates'

function byName (
  items: ExcursionCardUI[],
  name: Nullable<string>,
): ExcursionCardUI[] {
  return !name || name === ''
    ? items
    : items.filter((x: ExcursionCardUI) =>
      x.name.toLowerCase().includes(name.toLowerCase(), 0),
    )
}

function byTags (items: ExcursionCardUI[], tags: ExcursionTagItem[]): ExcursionCardUI[] {
  return tags.length === 0
    ? items
    : items.filter(
      (x: ExcursionCardUI) => x.tags.filter(y => tags.includes(y)).length > 0
    )
}

function byPrice (items: ExcursionCardUI[], price: number[]): ExcursionCardUI[] {
  return price.length !== 2
    ? items
    : items.filter(
      (x: ExcursionCardUI) => x.price.value >= price[0] && x.price.value <= price[1]
    )
}

function byDates (items: ExcursionCardUI[], excursionDates: PlannerExcursionDate[], viewDates: DateString[]): ExcursionCardUI[] {
  if (viewDates.length === 0){
    return items
  }
  return items.filter(x => {
    const d =  excursionDates.find(z => z.excursionOfferId === x.id)
    if (!d){
      return false
    }
    return viewDates.includes(d.selected)
  })
}

export function filterExcursion (items: ExcursionCardUI[], excursionDates: PlannerExcursionDate[], params: ExcursionFilterParams): ExcursionCardUI[] {
  const {
    name,
    tags,
    price,
    viewDates
  } = params
  const a = byName(items, name)
  const b = byTags(a, tags)
  const c =  byPrice(b, price)
  return byDates(c, excursionDates, viewDates)
}