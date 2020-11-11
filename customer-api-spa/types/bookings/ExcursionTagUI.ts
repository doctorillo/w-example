import { ExcursionTagItem } from './ExcursionTagItem'
import { Uuid } from '../basic/Uuid'

export interface ExcursionTagUI {
  id: Uuid;
  excursionId: Uuid;
  value: ExcursionTagItem;
  label: string;
}