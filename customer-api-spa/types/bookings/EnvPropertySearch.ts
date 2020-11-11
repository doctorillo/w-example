import { DateRange, initDateRange } from '../DateRange'
import { ContextItem } from '../basic/ContextItem'
import { ResultKind } from '../ResultKind'
import { QueryGroup, soloGroupInit } from './QueryGroup'
import { PriceViewMode } from '../PriceViewMode'
import { PointOption } from '../geo/PointOption'
import { Nullable } from '../Nullable'

export interface PropertySearchParams {
  name: string | null;
  stars: number[];
  price: number[];
  boardings: string[];
  amenities: string[];
  facilities: string[];
  medicals: string[];
  indications: string[];
  therapies: string[];
  viewStop: boolean;
  maxItems: number;
}

export function initPropertySearchParams (price: number[] = []): PropertySearchParams {
  return {
    name: null,
    stars: [],
    price: price,
    boardings: [],
    amenities: [],
    facilities: [],
    medicals: [],
    indications: [],
    therapies: [],
    viewStop: true,
    maxItems: 10
  }
}

export interface EnvPropertySearch<A> {
  id: string;
  ctx: ContextItem;
  point: Nullable<PointOption>;
  dates: Nullable<DateRange>;
  group: QueryGroup;
  boarding: Nullable<string>;
  status: ResultKind;
  items: A[];
  filtered: A[];
  price: number[];
  searchParams: PropertySearchParams;
  priceView: PriceViewMode;
}

export function initEnvPropertySearch<A> (id: string, ctx: ContextItem): EnvPropertySearch<A> {
  return {
    id: id,
    ctx: ctx,
    point: null,
    dates: initDateRange(),
    group: soloGroupInit(),
    boarding: null,
    status: ResultKind.Undefined,
    items: [],
    filtered: [],
    price: [0,0],
    searchParams: initPropertySearchParams(),
    priceView: PriceViewMode.PerRoom,
  }
}