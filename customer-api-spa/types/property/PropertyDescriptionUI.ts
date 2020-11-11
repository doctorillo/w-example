import { LangItem } from '../LangItem'
import { PropertyImageProduct } from '../bookings/PropertyImageProduct'
import { Nullable } from '../Nullable'

export interface PropertyDescriptionUI {
  id: string;
  lang: LangItem;
  description: Nullable<string>;
  paymentTerm: Nullable<string>;
  cancellationTerm: Nullable<string>;
  taxTerm: Nullable<string>;
  guestTerm: Nullable<string>;
  images: PropertyImageProduct[];
}