package bookingtour.protocols.actors.aggregators

import cats.Order
import zio.{RIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
trait Aggregate6Channels[Channel0, Channel1, Channel2, Channel3, Channel4, Channel5, State] {
  def run(
      tag: String,
      a: List[Channel0],
      b: List[Channel1],
      c: List[Channel2],
      d: List[Channel3],
      e: List[Channel4],
      f: List[Channel5],
      state: List[State]
  ): ZioResult[State]
}

object Aggregate6Channels {
  def make[Channel0, Channel1, Channel2, Channel3, Channel4, Channel5, State, Id](
      implicit fn: Aggregate6Fn[Channel0, Channel1, Channel2, Channel3, Channel4, Channel5, State],
      o1: Order[State],
      o2: Order[Id],
      r: State => Id
  ): Aggregate6Channels[Channel0, Channel1, Channel2, Channel3, Channel4, Channel5, State] =
    (
        tag: String,
        a: List[Channel0],
        b: List[Channel1],
        c: List[Channel2],
        d: List[Channel3],
        e: List[Channel4],
        f: List[Channel5],
        state: List[State]
    ) =>
      RIO
        .effect(fn.map(a, b, c, d, e, f))
        .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}"))
        .flatMap { received =>
          snapshotToResult[State, Id](
            actual = state,
            received = received
          )
        }
}
