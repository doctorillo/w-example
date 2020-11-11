package bookingtour.protocols.actors.aggregators

import cats.Order
import zio.{RIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
trait Aggregate4Channels[Channel0, Channel1, Channel2, Channel3, State] {
  def run(
      tag: String,
      a: List[Channel0],
      b: List[Channel1],
      c: List[Channel2],
      d: List[Channel3],
      state: List[State]
  ): ZioResult[State]
}

object Aggregate4Channels {
  def make[Channel0, Channel1, Channel2, Channel3, State, Id](
      implicit fn: Aggregate4Fn[Channel0, Channel1, Channel2, Channel3, State],
      o1: Order[State],
      o2: Order[Id],
      r: State => Id
  ): Aggregate4Channels[Channel0, Channel1, Channel2, Channel3, State] =
    (
        tag: String,
        a: List[Channel0],
        b: List[Channel1],
        c: List[Channel2],
        d: List[Channel3],
        state: List[State]
    ) =>
      RIO
        .effect(fn.map(a, b, c, d))
        .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}"))
        .flatMap { received =>
          snapshotToResult[State, Id](
            actual = state,
            received = received
          )
        }
}
