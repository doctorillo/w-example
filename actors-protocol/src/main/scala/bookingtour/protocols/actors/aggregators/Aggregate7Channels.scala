package bookingtour.protocols.actors.aggregators

import cats.Order
import zio.{RIO, ZIO}

/**
  * © Alexey Toroshchin 2020.
  */
trait Aggregate7Channels[
    Channel0,
    Channel1,
    Channel2,
    Channel3,
    Channel4,
    Channel5,
    Channel6,
    State
] {
  def run(
      tag: String,
      a: List[Channel0],
      b: List[Channel1],
      c: List[Channel2],
      d: List[Channel3],
      e: List[Channel4],
      f: List[Channel5],
      g: List[Channel6],
      state: List[State]
  ): ZioResult[State]
}

object Aggregate7Channels {
  def make[Channel0, Channel1, Channel2, Channel3, Channel4, Channel5, Channel6, State, Id](
      implicit fn: Aggregate7Fn[
        Channel0,
        Channel1,
        Channel2,
        Channel3,
        Channel4,
        Channel5,
        Channel6,
        State
      ],
      o1: Order[State],
      o2: Order[Id],
      r: State => Id
  ): Aggregate7Channels[
    Channel0,
    Channel1,
    Channel2,
    Channel3,
    Channel4,
    Channel5,
    Channel6,
    State
  ] =
    (
        tag: String,
        a: List[Channel0],
        b: List[Channel1],
        c: List[Channel2],
        d: List[Channel3],
        e: List[Channel4],
        f: List[Channel5],
        g: List[Channel6],
        state: List[State]
    ) =>
      RIO
        .effect(fn.map(a, b, c, d, e, f, g))
        .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}"))
        .flatMap { received =>
          snapshotToResult[State, Id](
            actual = state,
            received = received
          )
        }
}
