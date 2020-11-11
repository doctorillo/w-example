package bookingtour.protocols.actors.aggregators

import cats.Order
import zio.{RIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
trait Aggregate5Channels[Channel0, Channel1, Channel2, Channel3, Channel4, State] {
  def run(
      tag: String,
      a: List[Channel0],
      b: List[Channel1],
      c: List[Channel2],
      d: List[Channel3],
      e: List[Channel4],
      state: List[State]
  ): ZioResult[State]
}

object Aggregate5Channels {
  def make[Channel0, Channel1, Channel2, Channel3, Channel4, State, Id](
      implicit fn: Aggregate5Fn[Channel0, Channel1, Channel2, Channel3, Channel4, State],
      o1: Order[State],
      o2: Order[Id],
      r: State => Id
  ): Aggregate5Channels[Channel0, Channel1, Channel2, Channel3, Channel4, State] =
    (
        tag: String,
        a: List[Channel0],
        b: List[Channel1],
        c: List[Channel2],
        d: List[Channel3],
        e: List[Channel4],
        state: List[State]
    ) =>
      RIO
        .effect(fn.map(a, b, c, d, e))
        .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}"))
        .flatMap { received =>
          snapshotToResult[State, Id](
            actual = state,
            received = received
          )
        }
}
