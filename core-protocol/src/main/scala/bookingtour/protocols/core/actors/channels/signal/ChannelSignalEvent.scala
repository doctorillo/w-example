package bookingtour.protocols.core.actors.channels.signal

import java.util.UUID

import akka.actor.ActorRef
import bookingtour.protocols.core.actors.channels.ChannelStatus

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelSignalEvent(val channelId: UUID) extends Product with Serializable

object ChannelSignalEvent {
  final case class SignalChannelCreated(
      override val channelId: UUID,
      tag: String,
      producer: ActorRef,
      consumer: ActorRef
  ) extends ChannelSignalEvent(channelId)

  final case class SignalChannelDeleted(override val channelId: UUID) extends ChannelSignalEvent(channelId)

  final case class SignalChannelStatusChanged(
      override val channelId: UUID,
      status: ChannelStatus
  ) extends ChannelSignalEvent(channelId)
}
