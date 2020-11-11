import { DateRange } from '../../DateRange'

export interface AddAccommodation {
  dates: DateRange;
  groupId: string;
  priceUnitId: string;
}