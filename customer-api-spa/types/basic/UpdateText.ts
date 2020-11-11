import { LangItem } from '../LangItem'
import { Nullable } from '../Nullable'

export interface UpdateText {
  id: string;
  parentId: Nullable<string>;
  dataId: string;
  lang: LangItem;
  text: string;
}