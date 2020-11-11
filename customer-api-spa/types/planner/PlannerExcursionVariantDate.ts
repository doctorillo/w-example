import { DateString } from '../basic/DateString'
import { PlannerExcursionItem } from './PlannerExcursionItem'
import { parseLocalDate } from '../DateRange'

export interface PlannerExcursionVariantDate {
  date: DateString;
  variants: PlannerExcursionItem[];
  variantCount: number;
}

export function sortByDate(l: PlannerExcursionVariantDate, r: PlannerExcursionVariantDate): number {
  const ld = parseLocalDate(l.date)
  const rd = parseLocalDate(r.date)
  const cmp = ld.valueOf() - rd.valueOf()
  if (cmp === 0){
    return 0
  } else if (cmp < 0){
    return -1
  } else {
    return 1
  }
}