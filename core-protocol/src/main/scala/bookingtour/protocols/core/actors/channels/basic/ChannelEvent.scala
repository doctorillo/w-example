package bookingtour.protocols.core.actors.channels.basic

import java.util.UUID

import akka.actor.ActorRef
import bookingtour.protocols.core.actors.channels.ChannelStatus

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelEvent(val channelId: UUID) extends Product with Serializable

object ChannelEvent {
  final case class ChannelCreated(
      override val channelId: UUID,
      tag: String,
      producer: ActorRef,
      consumer: ActorRef
  ) extends ChannelEvent(channelId)

  final case class ChannelDeleted(override val channelId: UUID) extends ChannelEvent(channelId)

  final case class ChannelEmptySnapshotReceived(override val channelId: UUID) extends ChannelEvent(channelId)

  final case class ChannelSnapshotReceived[A](override val channelId: UUID, data: List[A])
      extends ChannelEvent(channelId)

  final case class ChannelItemCreated[A](override val channelId: UUID, data: List[A]) extends ChannelEvent(channelId)

  final case class ChannelItemUpdated[A](override val channelId: UUID, data: List[A]) extends ChannelEvent(channelId)

  final case class ChannelItemDeleted[A](override val channelId: UUID, data: List[A]) extends ChannelEvent(channelId)

  final case class ChannelStatusChanged(override val channelId: UUID, status: ChannelStatus)
      extends ChannelEvent(channelId)
}
