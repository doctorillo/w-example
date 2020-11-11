import { LabelAPI } from '../LabelAPI'

export interface ImageAPI {
  id: string;
  dataId: string;
  src: string;
  label: LabelAPI;
  position: number;
}