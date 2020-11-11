import { CurrencyItem } from './CurrencyItem'

export interface Amount {
  value: number;
  currency: CurrencyItem;
}