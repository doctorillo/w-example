import { ResultKind } from '../ResultKind'
import { Filter } from './Filter'
import { DataProps } from './DataProps'
import { Nullable } from '../Nullable'

export interface DataState<A, B> {
  status: ResultKind;
  extraEnv: B;
  filterExp: Nullable<string>;
  internal: A[];
  data: A[];
  pageData: A[];
  count: number;
  page: number;
  rowsPerPage: number;
}

export function initDataState<A, B> (
  extraEnv: B,
  status: ResultKind = ResultKind.Undefined,
  rowsPerPage = 10
): DataState<A, B> {
  return ({
    status: status,
    extraEnv: extraEnv,
    count: 0,
    filterExp: null,
    internal: [],
    data: [],
    pageData: [],
    page: 0,
    rowsPerPage: rowsPerPage,
  })
}

export function cleanDataState<A, B> (
  state: DataState<A, B>,
): DataState<A, B> {
  return Object.assign({}, state, {
    status: ResultKind.Undefined,
    count: 0,
    internal: [],
    data: [],
    pageData: [],
  })
}

export function startedDataState<A, B> (
  state: DataState<A, B>,
): DataState<A, B> {
  return Object.assign({}, state, {
    status: ResultKind.Start,
    count: 0,
    internal: [],
    data: [],
    pageData: [],
  })
}

export function fulfilledEmptyDataState<A, B> (
  state: DataState<A, B>,
): DataState<A, B> {
  return Object.assign({}, state, {
    status: ResultKind.Completed,
    count: 0,
    internal: [],
    data: [],
    page: 0,
    pageData: [],
  })
}

export function fulfillDataState<A, B> (
  state: DataState<A, B>,
  data: A[],
  filterFn: Nullable<Filter<A>>
): DataState<A, B> {
  const { filterExp, page, rowsPerPage, extraEnv } = state
  const _dataF: Array<A> = filterFn && filterExp ? data.filter(filterFn(filterExp)) : data
  const _badFilter = data.length > 0 && _dataF.length === 0
  const _data = _badFilter ? data : _dataF
  if (rowsPerPage === -1) {
    return Object.assign({}, state, {
      status: ResultKind.Completed,
      filterExp: _badFilter ? null : filterExp,
      count: data.length,
      internal: [...data],
      data: [..._data],
      page: 0,
      pageData: [..._data],
      extraEnv: {...extraEnv},
    })
  }
  const perPage = Math.min(_data.length, rowsPerPage)
  const items =
    perPage === 0
      ? []
      : _data.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
  return Object.assign({}, state, {
    status: ResultKind.Completed,
    internal: [...data],
    count: _data.length,
    filterExp: _badFilter ? null : filterExp,
    data: [..._data],
    page: page,
    pageData: [...items],
    extraEnv: {...extraEnv},
  })
}

export function filterDataState<A, B> (
  state: DataState<A, B>,
  fExp: Nullable<string>,
  filterFn: Nullable<Filter<A>>
): DataState<A, B> {
  const { internal, filterExp, rowsPerPage } = state
  const _fExp = !fExp ? null : fExp.toLowerCase()
  if (_fExp === filterExp) {
    return state
  }
  const _data = _fExp && filterFn ? internal.filter(filterFn(_fExp)) : internal
  if (rowsPerPage === -1) {
    return Object.assign({}, state, {
      count: _data.length,
      filterExp: _fExp,
      data: _data,
      page: 0,
      pageData: _data,
    })
  }
  const at = Math.min(_data.length, rowsPerPage)
  const _items = at === 0 ? [] : _data.slice(0, at)
  return Object.assign({}, state, {
    count: _data.length,
    filterExp: _fExp,
    data: _data,
    page: 0,
    pageData: _items,
  })
}

export function setPageDataState<A, B> (
  state: DataState<A, B>,
  page: number,
): DataState<A, B> {
  const { data, rowsPerPage } = state
  const pageData = data.slice(
    page * rowsPerPage,
    page * rowsPerPage + rowsPerPage,
  )
  return Object.assign({}, state, {
    page: page,
    pageData: pageData,
  })
}

export function setPageItemDataState<A, B> (
  state: DataState<A, B>,
  pageItems: number,
): DataState<A, B> {
  const { data, count } = state
  const pageData = count === 0 ? [] : data.slice(0, pageItems)
  return Object.assign({}, state, {
    page: 0,
    rowsPerPage: pageItems,
    pageData: pageData,
  })
}

export function dataStateToProps<A, B> (
  state: DataState<A, B>,
): DataProps<A> {
  return Object.assign(
    {},
    {
      status: state.status,
      extraEnv: state.extraEnv,
      items: state.pageData,
      count: state.count,
      page: state.page,
      rowsPerPage: state.rowsPerPage,
      filterExp: state.filterExp,
    },
  )
}

export function setExtraEnvDataState<A, B> (
  state: DataState<A, B>,
  extraEnv: B,
  filterFn: Nullable<Filter<A>>
): DataState<A, B> {
  return fulfillDataState(Object.assign({}, state, {
    extraEnv: extraEnv,
  }), state.internal, filterFn)
}

