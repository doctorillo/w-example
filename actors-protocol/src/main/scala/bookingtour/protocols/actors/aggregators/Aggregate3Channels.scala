package bookingtour.protocols.actors.aggregators

import cats.Order
import zio.{RIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
trait Aggregate3Channels[Channel0, Channel1, Channel2, State] {
  def run(
      tag: String,
      a: List[Channel0],
      b: List[Channel1],
      c: List[Channel2],
      state: List[State]
  ): ZioResult[State]
}

object Aggregate3Channels {
  def make[Channel0, Channel1, Channel2, State, Id](
      implicit fn: Aggregate3Fn[Channel0, Channel1, Channel2, State],
      o1: Order[State],
      o2: Order[Id],
      r: State => Id
  ): Aggregate3Channels[Channel0, Channel1, Channel2, State] =
    (
        tag: String,
        a: List[Channel0],
        b: List[Channel1],
        c: List[Channel2],
        state: List[State]
    ) =>
      RIO
        .effect(fn.map(a, b, c))
        .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}"))
        .flatMap { received =>
          snapshotToResult[State, Id](
            actual = state,
            received = received
          )
        }
}
