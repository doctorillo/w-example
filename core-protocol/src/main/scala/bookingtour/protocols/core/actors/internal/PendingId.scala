package bookingtour.protocols.core.actors.internal

import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.Order

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class PendingId[+A](val sequenceId: SequenceNr) extends Product with Serializable {
  def length: Long
}

object PendingId {
  final case class PendingTruncateId[A](override val sequenceId: SequenceNr) extends PendingId[A](sequenceId) {
    def length: Long = 0
  }

  final case class PendingUpsertId[A](override val sequenceId: SequenceNr, data: List[A])
      extends PendingId[A](sequenceId) {
    def length: Long = data.length
  }

  final case class PendingDeleteId[A](override val sequenceId: SequenceNr, data: List[A])
      extends PendingId[A](sequenceId) {
    def length: Long = data.length
  }

  trait ToOrderOps {
    implicit final def pendingIdO[A]: Order[PendingId[A]] =
      (x: PendingId[A], y: PendingId[A]) => x.sequenceId.x.compareTo(y.sequenceId.x)

    implicit final def pendingUpsertId[A]: Order[PendingUpsertId[A]] =
      (x: PendingUpsertId[A], y: PendingUpsertId[A]) => x.sequenceId.x.compareTo(y.sequenceId.x)

    implicit final def pendingDeleteId[A]: Order[PendingDeleteId[A]] =
      (x: PendingDeleteId[A], y: PendingDeleteId[A]) => x.sequenceId.x.compareTo(y.sequenceId.x)
  }

  final object order extends ToOrderOps
}
