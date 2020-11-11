package bookingtour.core.kafka

import java.util.UUID

import akka.Done
import akka.actor.{ActorContext, ActorRef}

/**
  * © Alexey Toroshchin 2019.
  */
trait MakeHeartbeatProducer {
  val consumerPoolRef: ActorRef
  val producer: MessageProducer[Done]
  val heartbeatPerSec: Long
  val messageTtlSec: Long
  val enableTrace: Boolean

  def make(
      manager: ActorRef,
      sessionId: UUID,
      targetTag: String,
      outputTopic: String,
      inputTopic: String
  )(implicit ctx: ActorContext): ActorRef
}
