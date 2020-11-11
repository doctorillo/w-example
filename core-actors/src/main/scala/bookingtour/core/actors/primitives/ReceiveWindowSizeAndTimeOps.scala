package bookingtour.core.actors.primitives

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

import akka.actor.{Actor, Timers}
import bookingtour.protocols.core.actors.operations.OpCommand.Start

/**
  * © Alexey Toroshchin 2019.
  */
trait ReceiveWindowSizeAndTimeOps {
  _: Actor with Timers =>
  val batchSize: Int
  val batchWindowMs: Int
  private final val timerKey: String = UUID.randomUUID().toString

  protected final def receiveWindowEvent(size: Long): Unit = {
    if (size >= batchSize) {
      self ! Start
    }
    cancelTimer()
    timers.startSingleTimer(
      timerKey,
      Start,
      FiniteDuration(batchWindowMs, TimeUnit.MILLISECONDS)
    )
  }

  protected final def timerActive(): Boolean = timers.isTimerActive(timers)

  protected final def cancelTimer(): Unit = {
    if (timerActive()) {
      timers.cancel(timerKey)
    }
  }
}
