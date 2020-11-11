package bookingtour.protocols.core.actors.internal

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class InternalCommand extends Product with Serializable

object InternalCommand {
  // final case class SetStamp[A](stamp: A) extends InternalCommand

  final case class DetachId[A](id: List[A]) extends InternalCommand

  final case class AttachId[A](id: List[A]) extends InternalCommand

  final case class AttachErrorId[A](id: List[A]) extends InternalCommand
}
