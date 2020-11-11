package bookingtour.core.actors.kafka.pool.hearbeat.consumer

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props, Stash, Timers}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.protocols.core.actors.kafka.EdgeHeartbeat
import bookingtour.protocols.core.register.RegisterEntity
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class HeartbeatConsumer private (
    val uniqueTag: String,
    val edgeRef: EdgeRef,
    val topic: String,
    val heartbeatTopic: String,
    val heartbeatInterval: Long,
    val enableTrace: Boolean
)(
    implicit val runtime: zio.Runtime[zio.ZEnv],
    val heartbeatEntity: RegisterEntity.Aux[EdgeHeartbeat]
) extends Actor with Stash with Timers with ActorLogging with State with HeartbeatBehavior with BasicBehavior {
  override def preStart(): Unit = {
    super.preStart()
    if (enableTrace) {
      log.info(s"$uniqueTag. topic: $heartbeatTopic. create.")
    }
    heartbeatBehavior()
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.error(s"$uniqueTag. {}.", reason)
    shutdown()
  }
}

object HeartbeatConsumer {
  @newtype final case class HeartbeatConsumerRef(x: ActorRef)

  final def make(
      edgeRef: EdgeRef,
      topic: String,
      heartbeatTopic: String,
      heartbeatInterval: Long,
      enableTrace: Boolean
  )(
      implicit ctx: ActorContext,
      r: zio.Runtime[zio.ZEnv],
      h: RegisterEntity.Aux[EdgeHeartbeat]
  ): HeartbeatConsumerRef = {
    val uniqueTag = s"heartbeat-consumer:$topic"
    ctx
      .actorOf(
        Props(
          new HeartbeatConsumer(
            uniqueTag = uniqueTag,
            edgeRef = edgeRef,
            topic = topic,
            heartbeatTopic = heartbeatTopic,
            heartbeatInterval = heartbeatInterval,
            enableTrace = enableTrace
          )
        ),
        uniqueTag
      )
      .coerce[HeartbeatConsumerRef]
  }
}
