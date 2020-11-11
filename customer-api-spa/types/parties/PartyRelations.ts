import { ContextItem } from '../basic/ContextItem'
import { PartyValue } from './PartyValue'

export interface PartyRelations {
  partyId: string;
  ctx: ContextItem;
  suppliers: PartyValue[];
  customers: PartyValue[];
}