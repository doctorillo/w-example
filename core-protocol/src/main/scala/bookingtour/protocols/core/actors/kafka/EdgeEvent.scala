package bookingtour.protocols.core.actors.kafka

import java.util.UUID

import akka.actor.ActorRef
import bookingtour.protocols.core.messages.MessageEnvelope.{EnvelopeChannel, EnvelopeSimple}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class EdgeEvent(val id: UUID, val replayTo: ActorRef) extends Product with Serializable

object EdgeEvent {
  final case class EdgeConsumerChannelCreated(
      override val id: UUID,
      override val replayTo: ActorRef
  ) extends EdgeEvent(id, replayTo)

  final case class EdgeProducerChannelCreated(
      override val id: UUID,
      override val replayTo: ActorRef
  ) extends EdgeEvent(id, replayTo)

  final case class EdgeChannelError(
      override val id: UUID,
      error: String,
      override val replayTo: ActorRef
  ) extends EdgeEvent(id, replayTo)

  final case class EdgeConsumerChannelDeleted(
      override val id: UUID,
      override val replayTo: ActorRef
  ) extends EdgeEvent(id, replayTo)

  final case class EdgeProducerChannelDeleted(
      override val id: UUID,
      override val replayTo: ActorRef
  ) extends EdgeEvent(id, replayTo)

  final case class EdgeChannelConsumerMessageReceived[A](
      override val id: UUID,
      envelope: EnvelopeChannel,
      msg: A,
      override val replayTo: ActorRef
  ) extends EdgeEvent(id, replayTo)

  final case class EdgeConsumerMessageReceived[A](
      override val id: UUID,
      envelope: EnvelopeSimple,
      msg: A,
      override val replayTo: ActorRef
  ) extends EdgeEvent(id, replayTo)

  final case class EdgeTopicUnreachableReceived(
      override val id: UUID,
      topic: String,
      override val replayTo: ActorRef
  ) extends EdgeEvent(id, replayTo)
}
