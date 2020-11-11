package bookingtour.protocols.actors.aggregators

import cats.Monoid
import cats.instances.list._
import cats.instances.map._
import cats.syntax.monoid._

/**
  * © Alexey Toroshchin 2019.
  */
final case class AggregatePartitionResult[Key, Value](
    created: List[Value],
    updated: List[Value],
    deleted: List[Value],
    state: Map[Key, List[Value]]
) {
  def values: List[Value] = state.foldLeft(List.empty[Value])((acc, x) => acc ++ x._2)

  def isEmpty: Boolean = created.isEmpty && updated.isEmpty && deleted.isEmpty && state.isEmpty

  def nonEmpty: Boolean = !isEmpty

  def length: Long = state.foldLeft(0L)((acc, x) => acc + x._2.length)
}

object AggregatePartitionResult {
  trait ToMonoidOps {
    implicit final def aggregatePartitionResultMO[Key, Value]: Monoid[
      AggregatePartitionResult[Key, Value]
    ] = new Monoid[AggregatePartitionResult[Key, Value]] {
      def empty: AggregatePartitionResult[Key, Value] = AggregatePartitionResult(
        created = List.empty,
        updated = List.empty,
        deleted = List.empty,
        state = Map.empty
      )

      def combine(
          x: AggregatePartitionResult[Key, Value],
          y: AggregatePartitionResult[Key, Value]
      ): AggregatePartitionResult[Key, Value] =
        AggregatePartitionResult(
          created = x.created |+| y.created,
          updated = x.updated |+| y.updated,
          deleted = x.deleted |+| y.deleted,
          state = x.state |+| y.state
        )
    }
  }

  final object monoid extends ToMonoidOps
}
