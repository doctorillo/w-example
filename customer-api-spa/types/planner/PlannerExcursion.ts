import { Uuid } from '../basic/Uuid'
import { ExcursionFilterParams, makeExcursionFilterParams } from './ExcursionFilterParams'
import { fromQueryGroup, QueryClientGroup } from '../bookings/QueryClientGroup'
import { QueryGroup } from '../bookings/QueryGroup'
import { v4 as uuid } from 'uuid'
import { PlannerExcursionDate } from './PlannerExcursionDates'
import { DateString } from '../basic/DateString'
import { PlannerExcursionVariantDate } from './PlannerExcursionVariantDate'
import { PlannerExcursionItemId } from './PlannerExcursionItem'

export interface PlannerExcursion {
  id: Uuid;
  queryGroup: QueryClientGroup;
  dates: DateString[];
  items: PlannerExcursionVariantDate[];
  selected: PlannerExcursionItemId[];
  variantCount: number;
  excursionDates: PlannerExcursionDate[];
  filter: ExcursionFilterParams;
  maxItems: number;
}

export const makePlannerExcursion = (queryGroup: QueryGroup): PlannerExcursion => {
  const cg = fromQueryGroup(queryGroup)
  return {
    id: uuid(),
    queryGroup: cg,
    dates: [],
    items: [],
    selected: [],
    variantCount: 0,
    excursionDates: [],
    filter: makeExcursionFilterParams(),
    maxItems: 10,
  }
}
