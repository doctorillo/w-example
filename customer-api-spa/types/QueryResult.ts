export interface QueryResult<A> {
  items: A[];
  size: number;
  hasError: boolean;
  debug: string[];
}