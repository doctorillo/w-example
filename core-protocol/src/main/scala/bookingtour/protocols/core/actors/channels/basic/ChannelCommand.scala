package bookingtour.protocols.core.actors.channels.basic

import java.util.UUID

import akka.actor.ActorRef
import bookingtour.protocols.core.actors.channels.ChannelStatus

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ChannelCommand(val channelId: UUID) extends Product with Serializable

object ChannelCommand {
  final case class ChannelCreate(
      override val channelId: UUID,
      tag: String,
      consumer: ActorRef
  ) extends ChannelCommand(channelId)

  final case class ChannelDelete(
      override val channelId: UUID,
      consumer: ActorRef
  ) extends ChannelCommand(channelId)

  final case class ChannelPushEmptySnapshot(
      override val channelId: UUID
  ) extends ChannelCommand(channelId)

  final case class ChannelPushSnapshot[A](
      override val channelId: UUID,
      data: List[A]
  ) extends ChannelCommand(channelId)

  final case class ChannelPushCreate[A](
      override val channelId: UUID,
      data: List[A]
  ) extends ChannelCommand(channelId)

  final case class ChannelPushUpdate[A](
      override val channelId: UUID,
      data: List[A]
  ) extends ChannelCommand(channelId)

  final case class ChannelPushDelete[A](
      override val channelId: UUID,
      data: List[A]
  ) extends ChannelCommand(channelId)

  final case class ChannelPushDeleteId[A](
      override val channelId: UUID,
      data: List[A]
  ) extends ChannelCommand(channelId)

  final case class ChannelPushStatus(
      override val channelId: UUID,
      status: ChannelStatus
  ) extends ChannelCommand(channelId)
}
