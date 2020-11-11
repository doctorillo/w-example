import { ResultKind } from '../ResultKind'
import { Filter } from './Filter'
import { Nullable } from '../Nullable'

export interface DataList<A> {
  status: ResultKind;
  filterFn: Nullable<Filter<A>>;
  filterExp: Nullable<string>;
  head: Nullable<A>;
  tail: A[];
  data: A[];
  pageData: A[];
  count: number;
  page: number;
  rowsPerPage: number;
}

export function initDataList<A> (
  status: ResultKind = ResultKind.Undefined,
  rowsPerPage = 10,
  filterFn: Nullable<Filter<A>> = null,
): DataList<A> {
  return ({
    status: status,
    count: 0,
    filterFn: filterFn,
    filterExp: null,
    head: null,
    tail: [],
    data: [],
    pageData: [],
    page: 0,
    rowsPerPage: rowsPerPage,
  })
}

function cleanAndStatus<A> (state: DataList<A>, status: ResultKind): DataList<A> {
  return {
    ...state,
    status: status,
    count: 0,
    head: null,
    tail: [],
    data: [],
    pageData: [],
  }
}

export function cleanDataList<A> (
  state: DataList<A>,
): DataList<A> {
  return cleanAndStatus(state, ResultKind.Undefined)
}

export function startedDataList<A> (
  state: DataList<A>,
): DataList<A> {
  return cleanAndStatus(state, ResultKind.Start)
}

export function fulfilledEmptyDataList<A> (
  state: DataList<A>,
): DataList<A> {
  return cleanAndStatus(state, ResultKind.Completed)
}

export function fulfillDataList<A> (
  state: DataList<A>,
  data: A[],
): DataList<A> {
  if (data.length === 0) {
    return fulfilledEmptyDataList(state)
  }
  const { filterFn, filterExp, page, rowsPerPage } = state
  const head: A = data[0]
  const tail: A[] = data.slice(1, data.length - 1)
  const _dataF: Array<A> = filterFn && filterExp ? tail.filter(filterFn(filterExp)) : tail
  const _badFilter = tail.length > 0 && _dataF.length === 0
  const _data = _badFilter ? tail : _dataF
  if (rowsPerPage === -1) {
    return {
      ...state,
      status: ResultKind.Completed,
      filterExp: _badFilter ? null : filterExp,
      count: tail.length,
      head: head,
      tail: tail,
      data: _data,
      page: 0,
      pageData: _data,
    }
  }
  const perPage = Math.min(_data.length, rowsPerPage)
  const items =
    perPage === 0
      ? []
      : _data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
  return Object.assign({}, state, {
    status: ResultKind.Completed,
    head: head,
    tail: tail,
    count: _data.length,
    filterExp: _badFilter ? null : filterExp,
    data: _data,
    page: page,
    pageData: items,
  })
}

export function filterDataList<A> (
  state: DataList<A>,
  fExp: Nullable<string>,
): DataList<A> {
  const { tail, filterFn, filterExp, rowsPerPage } = state
  const _fExp = !fExp ? null : fExp.toLowerCase()
  if (_fExp === filterExp) {
    return state
  }
  const _data = _fExp && filterFn ? tail.filter(filterFn(_fExp)) : tail
  if (rowsPerPage === -1) {
    return {
      ...state,
      count: _data.length,
      filterExp: _fExp,
      data: _data,
      page: 0,
      pageData: _data,
    }
  }
  const at = Math.min(_data.length, rowsPerPage)
  const _items = at === 0 ? [] : _data.slice(0, at)
  return {
    ...state,
    count: _data.length,
    filterExp: _fExp,
    data: _data,
    page: 0,
    pageData: _items,
  }
}

export function setPageDataList<A> (
  state: DataList<A>,
  page: number,
): DataList<A> {
  const { data, rowsPerPage } = state
  const pageData = data.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage,
  )
  return {
    ...state,
    page: page,
    pageData: pageData,
  }
}

export function setPageItemDataList<A> (
  state: DataList<A>,
  pageItems: number,
): DataList<A> {
  const { data, count } = state
  const pageData = count === 0 ? [] : data.slice(0, pageItems)
  return {
    ...state,
    page: 0,
    rowsPerPage: pageItems,
    pageData: pageData,
  }
}

export function swapHeadDataList<A> (
  state: DataList<A>,
  takeFn: (take: A) => boolean,
): DataList<A> {
  if (!state.head){
    return state
  }
  if (state.head && takeFn(state.head)){
    return state
  }
  const take = state.tail.find(takeFn)
  if (!take){
    return state
  }
  const _state: DataList<A> = {...state, head: take, tail: [state.head, ...state.tail.filter((x: A) => x !== take)]}
  return filterDataList(_state, _state.filterExp)
}

export function updateHeadDataList<A> (
  state: DataList<A>,
  head: A,
): DataList<A> {
  return {...state, head: head }
}

export function addDataList<A> (
  state: DataList<A>,
  el: A,
): DataList<A> {
  if (!state.head){
    return fulfillDataList(state, [el])
  }
  const tail = state.head ? [state.head, ...state.tail] : state.tail
  const _state: DataList<A> = {...state, head: el, tail}
  return filterDataList(_state, _state.filterExp)
}

export function removeDataList<A> (
  state: DataList<A>,
  takeFn: (take: A) => boolean,
): DataList<A> {
  const tail = state.tail.filter(x => !takeFn(x))
  const _state: DataList<A> = {...state, tail: tail}
  return filterDataList(_state, _state.filterExp)
}

