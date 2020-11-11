package bookingtour.core.actors.kafka.queries.client.query

import java.time.Instant
import java.util.UUID

import akka.actor.ActorRef

/**
  * © Alexey Toroshchin 2020.
  */
final case class QueueCell[A](
    envelopeId: UUID,
    query: A,
    expiredAt: Instant,
    consumer: ActorRef
)
