package bookingtour.core.actors.kafka.pool.hearbeat.consumer

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Timers}
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelConsumerMessageReceived,
  EdgeConsumerChannelCreated,
  EdgeTopicUnreachableReceived
}
import bookingtour.protocols.core.actors.operations.OpCommand.Timeout
import cats.instances.uuid._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[consumer] trait BasicBehavior {
  _: Actor with Timers with ActorLogging with State =>

  private def behaviors(
      consumer: EdgeConsumerChannelCreated
  ): Receive = {
    case Timeout =>
      log.error(s"$uniqueTag. heartbeat-topic: $heartbeatTopic. topic: $topic. time-out received.")
      edgeRef.x ! EdgeTopicUnreachableReceived(
        id = UUID.randomUUID(),
        topic = topic,
        replayTo = self
      )

    case msg: EdgeChannelConsumerMessageReceived[_] if msg.id === consumer.id =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. heartbeat-topic: $heartbeatTopic. topic: $topic. heartbeat received."
        )
      }
      timerStart()

    case msg =>
      log.error(s"$uniqueTag. heartbeat-topic: $heartbeatTopic. topic: $topic. unhandled $msg.")
      shutdown()
  }

  protected final def basicBehavior(
      consumer: EdgeConsumerChannelCreated
  ): Unit =
    context.become(
      behaviors(consumer)
    )
}
