package bookingtour.protocols.actors.aggregators

import bookingtour.protocols.core.types.FunctionKCore.instances._
import cats.Order
import zio.{Task, ZIO}

/**
  * © Alexey Toroshchin 2019.
  *
  * Map anf Filter fn
  *
  */
trait TransformChannel[Input, Value] {
  def run(tag: String, a: List[Input]): Task[List[Value]]
}

object TransformChannel {
  def makeFilter[Input, Value: Ordering](
      fn: Input => Option[Value]
  ): TransformChannel[Input, Value] =
    (
        tag: String,
        a: List[Input]
    ) => ZIO.effect(a.flatMap(fn(_).liftFK[List]).distinct)

  def makeMapper[Input, Value: Ordering](
      implicit fn: Input => Value
  ): TransformChannel[Input, Value] =
    (
        tag: String,
        a: List[Input]
    ) => ZIO.effect(a.map(fn(_)).distinct)

  def makeMany[Input, Value: Ordering](
      implicit fn: Input => List[Value]
  ): TransformChannel[Input, Value] =
    (
        tag: String,
        a: List[Input]
    ) => ZIO.effect(a.flatMap(x => fn(x)).distinct)

  def makeInputMany[Input, Value](
      implicit fn: List[Input] => List[Value],
      o: Order[Value]
  ): TransformChannel[Input, Value] =
    (
        tag: String,
        a: List[Input]
    ) => ZIO.effect(fn(a).distinct)
}
