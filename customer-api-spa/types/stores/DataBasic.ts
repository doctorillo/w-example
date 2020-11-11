import { ResultKind } from '../ResultKind'

export interface DataBasic<A> {
  status: ResultKind;
  data: A[];
  count: number;
}

export function dataBasicInit<A>(status: ResultKind): DataBasic<A> {
  return {
    status: status,
    data: [],
    count: 0,
  }
}

export function fulfillDataBasic<A>(data: A[]): DataBasic<A> {
  return {
    status: ResultKind.Completed,
    data: data,
    count: data.length,
  }
}
