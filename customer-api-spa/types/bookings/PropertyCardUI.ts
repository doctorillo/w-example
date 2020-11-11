import { LangItem } from '../LangItem'
import { GPoint } from '../geo/GPoint'
import { BestPriceUI } from './BestPriceUI'
import { PropertyImageProduct } from './PropertyImageProduct'
import { BoardingUI } from './BoardingUI'
import { Nullable } from '../Nullable'

export interface PropertyCardUI {
  id: string;
  lang: LangItem;
  partyId: string;
  supplierId: string;
  countryId: string;
  regionId: string;
  cityId: string;
  districtId: Nullable<string>;
  name: string;
  star: number;
  address: string;
  location: Nullable<GPoint>;
  images: PropertyImageProduct[];
  boardings: BoardingUI[];
  amenities: string[];
  facilities: string[];
  indications: string[];
  therapies: string[];
  bestPrice: BestPriceUI;
}