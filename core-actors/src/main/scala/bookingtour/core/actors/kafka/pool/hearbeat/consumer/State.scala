package bookingtour.core.actors.kafka.pool.hearbeat.consumer

import java.util.UUID

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Timers}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.protocols.core.actors.kafka.EdgeHeartbeat
import bookingtour.protocols.core.actors.operations.OpCommand.Timeout
import bookingtour.protocols.core.messages.TaggedChannel.ChannelTag
import bookingtour.protocols.core.messages._
import bookingtour.protocols.core.register.RegisterEntity

/**
  * © Alexey Toroshchin 2019.
  */
protected[consumer] trait State {
  _: Actor with Timers with ActorLogging =>

  val uniqueTag: String
  val edgeRef: EdgeRef
  val topic: String
  val heartbeatTopic: String
  val heartbeatInterval: Long
  val enableTrace: Boolean
  implicit val heartbeatEntity: RegisterEntity.Aux[EdgeHeartbeat]
  implicit val runtime: zio.Runtime[zio.ZEnv]

  private final val timerKey: String  = UUID.randomUUID().toString
  protected final val sessionId: UUID = UUID.randomUUID()
  implicit protected final val taggedChannel: TaggedChannel =
    ChannelTag(topic)

  protected final def timerStart(): Unit = {
    timerCancel()
    timers.startSingleTimer(timerKey, Timeout, (heartbeatInterval + 7).seconds)
  }

  protected final def timerCancel(): Unit =
    timers.cancel(timerKey)

  final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown.")
    }
    timerCancel()
    context.stop(self)
  }

  final def receive: Receive = Actor.emptyBehavior
}
