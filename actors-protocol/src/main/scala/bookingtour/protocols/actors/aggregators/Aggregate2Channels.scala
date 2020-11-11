package bookingtour.protocols.actors.aggregators

import cats.Order
import zio.{RIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
trait Aggregate2Channels[Channel0, Channel1, State] {
  def run(
      tag: String,
      a: List[Channel0],
      b: List[Channel1],
      state: List[State]
  ): ZioResult[State]
}

object Aggregate2Channels {
  def make[Channel0, Channel1, State, Id](
      implicit fn: Aggregate2Fn[Channel0, Channel1, State],
      o1: Order[State],
      o2: Order[Id],
      r: State => Id
  ): Aggregate2Channels[Channel0, Channel1, State] =
    (
        tag: String,
        a: List[Channel0],
        b: List[Channel1],
        state: List[State]
    ) =>
      RIO
        .effect(fn.map(a, b))
        .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}"))
        .flatMap { received =>
          snapshotToResult[State, Id](
            actual = state,
            received = received
          )
        }
}
