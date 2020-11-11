import { Uuid } from '../basic/Uuid'
import { Amount } from '../bookings/Amount'

export interface PlannerExcursionItemClient {
  clientId: Uuid;
  price: Amount;
}