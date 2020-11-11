package bookingtour.protocols.core.actors.internal

import scala.collection.immutable.Map

import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.Order

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class PendingItem(val id: SequenceNr) extends Product with Serializable

object PendingItem {
  final case class PendingSnapshot[Key, Value](
      override val id: SequenceNr,
      run: Map[Key, List[Value]] => Unit
  ) extends PendingItem(id)

  final case class PendingUpsert[Key, Value](
      override val id: SequenceNr,
      run: Map[Key, List[Value]] => Unit
  ) extends PendingItem(id)

  final case class PendingDelete[Key, Value](
      override val id: SequenceNr,
      run: Map[Key, List[Value]] => Unit
  ) extends PendingItem(id)

  implicit final def pendingSnapshotO[Key, Value]: Order[PendingSnapshot[Key, Value]] =
    (x: PendingSnapshot[Key, Value], y: PendingSnapshot[Key, Value]) => x.id.x.compareTo(y.id.x)

  implicit final def pendingUpsertO[Key, Value]: Order[PendingUpsert[Key, Value]] =
    (x: PendingUpsert[Key, Value], y: PendingUpsert[Key, Value]) => x.id.x.compareTo(y.id.x)

  implicit final def pendingDeleteO[Key, Value]: Order[PendingDelete[Key, Value]] =
    (x: PendingDelete[Key, Value], y: PendingDelete[Key, Value]) => x.id.x.compareTo(y.id.x)

  implicit final val pendingItemO: Order[PendingItem] =
    (x: PendingItem, y: PendingItem) => x.id.x.compareTo(y.id.x)

  implicit final val pendingItemOrd: Ordering[PendingItem] = pendingItemO.toOrdering
}
