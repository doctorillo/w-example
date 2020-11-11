import { ResultKind } from '../ResultKind'
import { Nullable } from '../Nullable'

export interface DataItem<A, B> {
  status: ResultKind;
  extraEnv: Nullable<B>;
  item: Nullable<A>;
}

export function initDataItem<A, B> (
  status: ResultKind,
  item: Nullable<A>,
  extraEnv: Nullable<B>,
): DataItem<A, B> {
  return ({
    status: status,
    extraEnv: extraEnv,
    item: item,
  })
}

export function startedDataItem<A, B> (
  state: DataItem<A, B>,
): DataItem<A, B> {
  return Object.assign({}, state, {
    status: ResultKind.Start,
  })
}

export function cleanDataItem<A, B> (state: DataItem<A, B>): DataItem<A, B> {
  return Object.assign({}, state, {
    status: ResultKind.Undefined,
    item: null,
  })
}

export function setExtraEnvDataItem<A, B> (state: DataItem<A, B>, extraEnv: Nullable<B>): DataItem<A, B> {
  return Object.assign({}, state, {
    extraEnv: extraEnv,
  })
}

export const fulfillDataItem = <A, B> (
  state: DataItem<A, B>,
  data: A,
): DataItem<A, B> => {
  return Object.assign({}, state, {
    status: ResultKind.Completed,
    item: data,
  })
}

