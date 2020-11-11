import { ResultKind } from '../ResultKind'
import { Nullable } from '../Nullable'

export interface DataProps<A> {
  status: ResultKind;
  items: A[];
  count: number;
  page: number;
  rowsPerPage: number;
  filterExp: Nullable<string>;
}