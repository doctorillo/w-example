import { LangItem } from '../LangItem'

export interface UpdateEnumLabel {
  id: string;
  value: number;
  dataId: string;
  lang: LangItem;
  text: string;
}