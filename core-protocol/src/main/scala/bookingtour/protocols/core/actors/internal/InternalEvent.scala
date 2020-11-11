package bookingtour.protocols.core.actors.internal

import java.util.UUID

import cats.data.NonEmptyChain

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class InternalEvent extends Product with Serializable

object InternalEvent {
  final case class CompleteReceived(id: UUID)          extends InternalEvent
  final case class Upserted[A](data: NonEmptyChain[A]) extends InternalEvent
  final case class Deleted[A](data: NonEmptyChain[A])  extends InternalEvent
}
