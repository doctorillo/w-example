import{ PriceUnitUI } from '../property/prices/PriceUnitUI'
import { ResultKind } from '../ResultKind'
import { PropertyDescriptionUI } from '../property/PropertyDescriptionUI'

export interface EnvProperty {
  id: string;
  supplierId: string;
  customerId: string;
  description: PropertyDescriptionUI | null;
  descriptionStatus: ResultKind;
  prices: PriceUnitUI[];
  priceStatus: ResultKind;
}

export function initEnvProperty(id: string): EnvProperty {
  return {
    id: id,
    supplierId: id,
    customerId: id,
    description: null,
    descriptionStatus: ResultKind.Undefined,
    prices: [],
    priceStatus: ResultKind.Undefined,
  }
}