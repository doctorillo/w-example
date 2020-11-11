package bookingtour.core.actors.primitives.upserters.batch

import java.util.UUID

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Timers}
import bookingtour.core.doobie.queries.BatchCreateVoid
import bookingtour.core.doobie.queries.BatchCreateVoid
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core.actors.operations.OpCommand.Start
import cats.Order

/**
  * © Alexey Toroshchin 2019.
  */
protected[batch] trait State[Value, Id] {
  _: Actor with ActorLogging with Timers =>

  protected val uniqueTag: String
  protected val producer0: ActorProducer[Value, Id]
  protected val runUpsert: BatchCreateVoid[Value]
  // protected val runDelete: RunNecUpdate[Id]
  protected val batchSize: Int
  protected val enableTrace: Boolean

  implicit protected val paramR: Value => Id
  implicit protected val paramO: Order[Value]
  implicit protected val paramIdO: Order[Id]

  protected final val channelId: UUID = UUID.randomUUID()
  private final val timerKey: String  = UUID.randomUUID().toString

  protected final def timerActive(): Boolean = timers.isTimerActive(timerKey)

  protected final def cancelTimer(): Unit = {
    if (timers.isTimerActive(timerKey)) {
      timers.cancel(timerKey)
    }
  }

  protected final def startTimer(): Unit = {
    cancelTimer()
    timers.startSingleTimer(timerKey, Start, 250.milliseconds)
  }

  protected final def ifReady(items: Long): Unit = {
    if (items >= batchSize) {
      startTimer()
      self ! Start
    } else if (!timerActive()) {
      startTimer()
    }
  }

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown")
    }
    context.stop(self)
  }
}
