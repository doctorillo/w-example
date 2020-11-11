import { PropertyPoint } from './PropertyPoint'
import { MapViewPortProps } from './MapViewPortProps'

export interface PropertyMapProps {
  viewport: MapViewPortProps;
  actual: PropertyPoint;
  otherPoints: PropertyPoint[];
  toProperty: (a: string, b: string, c: string) => void;
}