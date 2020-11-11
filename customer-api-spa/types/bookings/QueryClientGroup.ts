import { Uuid } from '../basic/Uuid'
import { QueryClient } from './QueryClient'
import { QueryGroup } from './QueryGroup'

export interface QueryClientGroup {
  id: Uuid;
  clients: QueryClient[];
}

export interface ClientReduce {
  idx: number;
  acc: QueryClient[];
}

export const fromQueryGroup = (x: QueryGroup): QueryClientGroup => {
  const xs: ClientReduce = x.rooms.reduce((a, x) => {
    const ys = x.guests.reduce((b, y) => ({
      idx: b.idx + 1,
      acc: [...b.acc, {
        age: y.age,
        position: b.idx + 1
      }]
    }), {idx: a.idx, acc: []} as ClientReduce)
    return {
      idx: ys.idx,
      acc: [...a.acc, ...ys.acc]
    }
  }, {idx: -1, acc: []} as ClientReduce)
  return {
    id: x.id,
    clients: xs.acc,
  }
}