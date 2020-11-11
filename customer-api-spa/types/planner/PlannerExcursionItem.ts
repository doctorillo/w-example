import { Uuid } from '../basic/Uuid'
import { Amount } from '../bookings/Amount'
import { PointUI } from '../geo/PointUI'
import { DateString } from '../basic/DateString'
import { PlannerExcursionItemClient } from './PlannerExcursionItemClient'
import { parseLocalDate, parseLocalTime } from '../DateRange'

export type PlannerExcursionItemId = string

export interface PlannerExcursionItem {
  id: PlannerExcursionItemId;
  created: DateString;
  updated: DateString;
  date: DateString;
  excursionId: Uuid;
  excursionName: string;
  accommodationPax: number;
  excursionOfferId: Uuid;
  pickupPoint: PointUI;
  startTime: string;
  duration: number;
  total: Amount;
  clients: PlannerExcursionItemClient[];
}

export function sortByDateTime(l: PlannerExcursionItem, r: PlannerExcursionItem): number {
  const ld = parseLocalDate(l.date)
  const rd = parseLocalDate(r.date)
  const cmpDate = ld.valueOf() - rd.valueOf()
  if (cmpDate < 0){
    return -1
  } else if (cmpDate > 0) {
    return 1
  }
  const lt = parseLocalTime(l.startTime)
  const rt = parseLocalTime(r.startTime)
  const cmpTime = lt.valueOf() - rt.valueOf()
  if (cmpTime === 0){
    return 0
  } else if (cmpTime < 0){
    return -1
  } else {
    return 1
  }
}
