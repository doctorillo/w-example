export type Nullable<A> = A | null


export function reduceNullable<A>(xs: Nullable<A>[]): A[] {
  return xs.reduce((acc: A[], x: Nullable<A>) => {
    if (!x){
      return acc
    } else {
      return [...acc, x]
    }
  }, [])
}