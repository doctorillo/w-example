import { Nullable } from '../Nullable'
import { ExcursionTagItem } from '../bookings/ExcursionTagItem'
import { DateString } from '../basic/DateString'

export interface ExcursionFilterParams {
  name: Nullable<string>;
  price: number[];
  tags: ExcursionTagItem[];
  viewDates: DateString[];
}

export function makeExcursionFilterParams(): ExcursionFilterParams {
  return {
    name: null,
    price: [],
    tags: [],
    viewDates: [],
  }
}
