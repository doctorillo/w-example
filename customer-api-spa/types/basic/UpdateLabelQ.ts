import { LangItem } from '../LangItem'
import { Nullable } from '../Nullable'

export interface UpdateLabelQ {
  id: string;
  parentId: Nullable<string>;
  dataId: string;
  lang: LangItem;
  label: string;
}