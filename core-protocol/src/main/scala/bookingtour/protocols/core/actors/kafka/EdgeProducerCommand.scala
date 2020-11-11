package bookingtour.protocols.core.actors.kafka

import java.time.Instant
import java.util.UUID

import bookingtour.protocols.core.messages.{MessageEnvelope, TaggedChannel}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class EdgeProducerCommand[T](
    val id: UUID,
    val channel: TaggedChannel,
    val msg: T
) extends Product with Serializable

object EdgeProducerCommand {
  final case class EdgePublish[T](
      override val id: UUID,
      override val channel: TaggedChannel,
      override val msg: T,
      expiredAt: Instant
  ) extends EdgeProducerCommand[T](id, channel, msg)

  final case class EdgePublishWithEnvelope[T](
      override val id: UUID,
      override val channel: TaggedChannel,
      envelope: MessageEnvelope,
      override val msg: T
  ) extends EdgeProducerCommand[T](id, channel, msg)
}
