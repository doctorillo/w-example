import { DateRange } from '../DateRange'
import { Uuid } from '../basic/Uuid'

export interface ExcursionOfferUI {
  id: Uuid;
  provider: Uuid;
  providerParty: Uuid;
  providerSync: Uuid;
  offer: Uuid;
  offerSync: Uuid;
  offerDate: Uuid;
  excursion: Uuid;
  excursionSync: Uuid;
  city: Uuid;
  pickupPoint: Uuid;
  pointNames: string;
  dates: DateRange;
  startTime: Date;
  finishTime: Date;
  days: number[];
}