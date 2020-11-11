import { Nullable } from '../Nullable'

export interface PropertyFilterParams {
  name: Nullable<string>;
  stars: number[];
  price: number[];
  boardings: string[];
  amenities: string[];
  facilities: string[];
  medicals: string[];
  indications: string[];
  therapies: string[];
  viewStop: boolean;
}

export function makePropertyFilterParams(): PropertyFilterParams {
  return {
    name: null,
    stars: [],
    price: [],
    boardings: [],
    amenities: [],
    facilities: [],
    medicals: [],
    indications: [],
    therapies: [],
    viewStop: true,
  }
}
