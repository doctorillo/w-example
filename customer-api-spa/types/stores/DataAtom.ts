import { ResultKind } from '../ResultKind'
import { Sort } from './Sort'
import { Filter } from './Filter'
import { Nullable } from '../Nullable'

export interface DataAtom<A, B> {
  status: ResultKind;
  extraEnv: Nullable<B>;
  internal: A[];
  data: A[];
  count: number;
  filterExp: Nullable<string>;
}

export function initDataAtom<A, B> (status: ResultKind, extraEnv: Nullable<B>): DataAtom<A, B> {
  return ({
    status,
    extraEnv,
    internal: [],
    data: [],
    count: 0,
    filterExp: null,
  })
}

export function startedDataAtom<A, B> (state: DataAtom<A, B>): DataAtom<A, B> {
  return {...state,  status: ResultKind.Start }
}

export function cleanDataAtom<A, B> (state: DataAtom<A, B>): DataAtom<A, B> {
  return {...state, ...{status: ResultKind.Undefined,
      internal: [],
      data: [],
      selected: null,
      count: 0,}}
}

export function fulfillDataAtom<A, B> (state: DataAtom<A, B>, data: A[], filterFn: Nullable<Filter<A>>, sortFn: Nullable<Sort<A>>): DataAtom<A, B> {
  const { filterExp } = state
  const _sorted = sortFn ? data.sort(sortFn) : data
  const _filtered = filterFn && filterExp ? _sorted.filter(filterFn(filterExp)) : _sorted
  return Object.assign({}, state, {
    status: ResultKind.Completed,
    internal: _sorted,
    data: _filtered,
    count: _filtered.length,
  })
}

export function filterDataAtom<A, B> (state: DataAtom<A, B>, filterExp: Nullable<string>, filterFn: Nullable<Filter<A>>): DataAtom<A, B> {
  const { internal } = state
  const _filtered = filterFn && filterExp ? internal.filter(filterFn(filterExp)) : internal
  return Object.assign({}, state, {
    data: _filtered,
    count: _filtered.length,
  })
}


