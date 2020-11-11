import { LabelAPI } from '../LabelAPI'
import { ExcursionTagItem } from './ExcursionTagItem'
import { Uuid } from '../basic/Uuid'
import { LangItem } from '../LangItem'
import { Nullable } from '../Nullable'
import { ExcursionTagUI } from './ExcursionTagUI'

export interface ExcursionTagProduct {
  id: Uuid;
  excursionId: Uuid;
  value: ExcursionTagItem;
  labels: LabelAPI[];
}

export const toUI = (x: LangItem) => (y: ExcursionTagProduct): Nullable<ExcursionTagUI> => {
  const label = y.labels.find(z => z.lang === x)
  if (!label) {
    return null
  } else {
    return { ...y, ...{ label: label.label } }
  }
}