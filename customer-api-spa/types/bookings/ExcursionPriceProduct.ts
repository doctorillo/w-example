import { Amount } from './Amount'
import { IntRange } from '../IntRange'
import { Uuid } from '../basic/Uuid'

export interface ExcursionPriceProduct {
  id: Uuid;
  excursion: Uuid;
  groupId: Uuid;
  clientId: Uuid;
  age: IntRange;
  amount: Amount;
}