import { Uuid } from '../basic/Uuid'
import { LangItem } from '../LangItem'
import { ImageAPI } from './ImageAPI'
import { ExcursionTagItem } from './ExcursionTagItem'
import { PointUI } from '../geo/PointUI'
import { WeekDay } from '../WeekDay'
import { IntRange } from '../IntRange'
import { Amount } from './Amount'
import { PlannerExcursionDate } from '../planner/PlannerExcursionDates'
import { parseLocalDate, parseLocalTime } from '../DateRange'

export interface ExcursionCardUI {
  id: Uuid;
  excursionId: Uuid;
  lang: LangItem;
  name: string;
  description: string;
  clientTerms: string;
  paymentTerms: string;
  cancellationTerms: string;
  taxTerms: string;
  images: ImageAPI[];
  tags: ExcursionTagItem[];
  pickupPoint: PointUI;
  dates: string[];
  accommodationPax: number;
  startTime: string;
  duration: number;
  days: WeekDay[];
  age: IntRange;
  price: Amount;
}


export const orderByDate = (eds: PlannerExcursionDate[]) => (l: ExcursionCardUI, r: ExcursionCardUI) => {
  const leftDateString = eds.find(x => x.excursionOfferId === l.id)
  const rightDateString = eds.find(x => x.excursionOfferId === r.id)
  if (!leftDateString || !rightDateString){
    return 0
  }
  const leftDate = parseLocalDate(leftDateString.selected).valueOf()
  const rightDate = parseLocalDate(rightDateString.selected).valueOf()
  const compareDates = leftDate - rightDate
  if (compareDates < 0){
    return -1
  } else if (compareDates > 0) {
    return 1
  } else {
    const leftTime = parseLocalTime(l.startTime).valueOf()
    const rightTime = parseLocalTime(r.startTime).valueOf()
    return leftTime - rightTime
  }
}