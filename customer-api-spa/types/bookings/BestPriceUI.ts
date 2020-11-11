import { LangItem } from '../LangItem'
import { Amount } from './Amount'
import { Nullable } from '../Nullable'

export interface BestPriceUI {
  priceUnitId: string;
  lang: LangItem;
  roomType: string;
  roomCategory: string;
  boarding: string;
  tariff: string;
  nights: number;
  pax: number;
  stopSale: boolean;
  resultCount: number;
  price: Amount;
  discount: Nullable<Amount>;
  total: Amount;
}