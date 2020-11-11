import { Nullable } from '../Nullable'
import { LangItem } from '../LangItem'

export interface ExtraSelect<A> {
  customerId: string;
  lang: LangItem;
  selected: Nullable<A>;
}