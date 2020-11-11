package bookingtour.protocols.core.actors.channels.signal

import java.util.UUID

import akka.actor.ActorRef
import bookingtour.protocols.core.actors.channels.ChannelStatus

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelSignalMutationEvent(val channelId: UUID) extends Product with Serializable

object ChannelSignalMutationEvent {
  final case class SignalChannelMutationReceived(
      override val channelId: UUID,
      status: ChannelStatus,
      producer: ActorRef
  ) extends ChannelSignalMutationEvent(channelId)
}
