export type BaseReduxAction<A> = {
  type: string;
  payload: A | A[] | null;
}