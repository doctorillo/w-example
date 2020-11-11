package bookingtour.core.actors.primitives

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

import akka.actor.{Actor, Timers}
import bookingtour.protocols.core.actors.operations.OpCommand.Start

/**
  * © Alexey Toroshchin 2019.
  */
trait ReceiveWindowOps {
  _: Actor with Timers =>
  val windowSec: Int
  private final val timerKey: String = UUID.randomUUID().toString

  protected final def receiveWindowEvent(): Unit = {
    cancelTimer()
    timers.startSingleTimer(
      timerKey,
      Start,
      FiniteDuration(windowSec, TimeUnit.SECONDS)
    )
  }

  protected final def cancelTimer(): Unit = {
    if (timers.isTimerActive(timerKey)) {
      timers.cancel(timerKey)
    }
  }
}
