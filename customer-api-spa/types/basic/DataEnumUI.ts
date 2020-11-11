import { LabelAPI } from '../LabelAPI'
import { Nullable } from '../Nullable'

export interface DataEnumUI {
  id: Nullable<string>;
  dataId: string;
  valueId: string;
  label: LabelAPI;
}
