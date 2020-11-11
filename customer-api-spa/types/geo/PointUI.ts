import { LabelAPI } from '../LabelAPI'
import { PointItem } from './PointItem'
import { Nullable } from '../Nullable'

export type PointUI = {
  id: string;
  parent: Nullable<string>;
  label: LabelAPI;
  category: PointItem;
}