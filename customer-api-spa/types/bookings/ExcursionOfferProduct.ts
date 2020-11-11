import { LabelAPI } from '../LabelAPI'
import { DateRange } from '../DateRange'
import { Uuid } from '../basic/Uuid'
import { ExcursionOfferUI } from './ExcursionOfferUI'
import { LangItem } from '../LangItem'
import { Nullable } from '../Nullable'

export interface ExcursionOfferProduct {
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
  pointNames: LabelAPI[];
  dates: DateRange;
  startTime: Date;
  finishTime: Date;
  days: number[];
}

export const toUI = (x: LangItem) => (y: ExcursionOfferProduct): Nullable<ExcursionOfferUI> => {
  const point = y.pointNames.find(z => z.lang === x)
  if (!point) {
    return null
  } else {
    return { ...y, ...{ pointNames: point.label } }
  }
}