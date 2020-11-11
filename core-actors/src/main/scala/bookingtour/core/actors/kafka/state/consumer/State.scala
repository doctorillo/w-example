package bookingtour.core.actors.kafka.state.consumer

import java.time.Instant
import java.util.UUID

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Timers}
import bookingtour.protocols.core.actors.operations.OpCommand.ReConnect
import bookingtour.protocols.core.messages._

/**
  * © Alexey Toroshchin 2019.
  */
protected[consumer] trait State[Value, Id] {
  _: Actor with Timers with ActorLogging =>

  val uniqueTag: String
  val outputTopic: String
  val connectTimeoutSeconds: Long
  val enableTrace: Boolean

  protected final val channelStateTag: String = s"$uniqueTag:state"
  protected final val dropBefore: Instant     = Instant.now()
  protected final val postOffice: PostOffice  = PostOffice(uniqueTag)
  protected final val timerKey: String        = UUID.randomUUID().toString

  protected def timerActive(): Boolean = timers.isTimerActive(timerKey)

  protected def timerCancel(): Unit = timers.cancel(timerKey)

  protected def timerStart(): Unit = {
    timerCancel()
    timers.startSingleTimer(timerKey, ReConnect, connectTimeoutSeconds.seconds)
  }

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown.")
    }
    context.stop(self)
  }
}
