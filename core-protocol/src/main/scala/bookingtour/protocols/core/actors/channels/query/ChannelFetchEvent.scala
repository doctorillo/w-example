package bookingtour.protocols.core.actors.channels.query

import java.util.UUID

import bookingtour.protocols.core.actors.channels.ChannelStatus

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelFetchEvent(val channelId: UUID) extends Product with Serializable

object ChannelFetchEvent {
  final case class StatusReceived[A](override val channelId: UUID, status: ChannelStatus)
      extends ChannelFetchEvent(channelId)

  final case class ErrorReceived(override val channelId: UUID, cause: List[Throwable])
      extends ChannelFetchEvent(channelId)

  final case class EmptyReceived(override val channelId: UUID) extends ChannelFetchEvent(channelId)

  final case class AnswerReceived[A](override val channelId: UUID, data: List[A]) extends ChannelFetchEvent(channelId)
}
