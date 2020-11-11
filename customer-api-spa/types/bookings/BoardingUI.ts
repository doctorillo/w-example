import { LabelAPI } from '../LabelAPI'

export interface BoardingUI {
  id: string;
  code: string;
  withTreatment: boolean;
  label: LabelAPI;
}