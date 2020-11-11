package bookingtour.core.actors.kafka.pool.hearbeat.consumer

import java.time.Instant

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeChannelError, EdgeConsumerChannelCreated}
import bookingtour.protocols.core.actors.kafka.EdgeHeartbeat
import cats.instances.string._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[consumer] trait HeartbeatBehavior {
  _: Actor with Stash with ActorLogging with State with BasicBehavior =>

  private val tag: String =
    s"$uniqueTag. heartbeat-behavior. heartbeat-topic: $heartbeatTopic. topic: $topic"

  private final def behaviors(): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag. created.")
      }
      unstashAll()
      basicBehavior(msg)
      timerStart()

    case msg: EdgeChannelError =>
      log.error(s"$tag. ${msg.error}.")
      shutdown()

    case _ =>
      stash()
  }

  protected final def heartbeatBehavior(): Unit = {
    context.become(behaviors())
    val msg = KafkaEdge.>.makeConsumerChannel[EdgeHeartbeat](
      uniqueTag = uniqueTag,
      topic = heartbeatTopic,
      register = heartbeatEntity,
      filter = _.topic === topic,
      dropBefore = Instant.now(),
      replayTo = self
    )
    edgeRef.x ! msg(self)
  }
}
