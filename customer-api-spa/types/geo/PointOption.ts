import { PointItem } from './PointItem'
import { PointUI } from './PointUI'

export type PointOption = {
  value: string;
  category: PointItem;
  label: string;
}

export function toPointOption(x: PointUI): PointOption {
  return {
    value: x.id,
    category: x.category,
    label: x.label.label
  }
}