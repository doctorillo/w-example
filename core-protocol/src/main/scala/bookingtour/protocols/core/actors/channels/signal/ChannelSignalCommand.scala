package bookingtour.protocols.core.actors.channels.signal

import java.util.UUID

import akka.actor.ActorRef

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelSignalCommand(val channelId: UUID) extends Product with Serializable

object ChannelSignalCommand {
  final case class SignalChannelCreate(
      override val channelId: UUID,
      tag: String,
      consumer: ActorRef
  ) extends ChannelSignalCommand(channelId)

  final case class SignalChannelDelete(override val channelId: UUID, consumer: ActorRef)
      extends ChannelSignalCommand(channelId)
}
