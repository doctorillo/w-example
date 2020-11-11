package bookingtour.core.actors.primitives.transforms.two

import java.util.UUID

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, Timers}
import bookingtour.core.actors.primitives.transforms.{cbArrow, CheckDrain}
import bookingtour.protocols.actors.aggregators.{Aggregate2Channels, AggregateResult}
import bookingtour.protocols.actors.channels.{MakeChannel, MakeSignalChannel}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.query.ChannelFetchCommand.actors._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalMutationCommand.actors._
import cats.syntax.semigroup._
import cats.{Order, Semigroup}
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
protected[two] trait State[Input0, Id0, Input1, Id1, Output, OutputId] {
  _: Actor with Timers with ActorLogging =>

  protected val uniqueTag: String
  protected val makeChannel0: MakeSignalChannel
  protected val makeChannel1: MakeSignalChannel
  protected val makeChannelState: MakeChannel[Output, OutputId]
  protected val merge: Aggregate2Channels[Input0, Input1, Output]
  protected val channelMayBeEmpty: Boolean
  val zioRuntime: zio.Runtime[zio.ZEnv]
  protected val enableTrace: Boolean

  implicit val statusO: Order[ChannelStatus]
  implicit val statusSG: Semigroup[ChannelStatus]

  protected final val channelId0: UUID        = UUID.randomUUID()
  protected final def channelTag0: String     = s"$uniqueTag:ch-0"
  protected final val channelId1: UUID        = UUID.randomUUID()
  protected final def channelTag1: String     = s"$uniqueTag:ch-1"
  protected final val channelIdState: UUID    = UUID.randomUUID()
  protected final def channelTagState: String = s"$uniqueTag:ch-state"

  private final val drainTimerKey = UUID.randomUUID()
  private final val drainDuration = 30.seconds

  protected final def cancelDrainTimer(): Unit = {
    if (timers.isTimerActive(drainTimerKey)) {
      timers.cancel(drainTimerKey)
    }
  }

  private final def startDrainTimer(): Unit =
    timers.startSingleTimer(drainTimerKey, CheckDrain, drainDuration)

  protected def calculateStatus(ch0: ChannelStatus, ch1: ChannelStatus): ChannelStatus = {
    if (channelMayBeEmpty) {
      ch0
    } else {
      ch0 |+| ch1
    }
  }

  private final def onChangeMayBeEmpty(
      ch0: SignalChannelCreated,
      ch1: SignalChannelCreated,
      state: ChannelCreated
  ): ZIO[Any, String, AggregateResult[Output]] = {
    for {
      ch0Producer <- askProducer[Id0](ch0.producer, channelId0, channelTag0)
      ch1Producer <- askProducer[Id1](ch1.producer, channelId1, channelTag1)
      dataCh0     <- askDataIfNotRunning[Input0](ch0Producer, channelId0, channelTag0)
      dataCh1     <- askDataIfNotRunning[Input1](ch1Producer, channelId1, channelTag1)
      dataState   <- askDataIfNotRunning(state.producer, channelIdState, channelTagState)
      _ <- ZIO
            .effectTotal(
              log.info(
                s"$uniqueTag. on-change. ch-0: ${dataCh0.length}. ch-1: ${dataCh1.length}. state: ${dataState.length}."
              )
            )
            .when(enableTrace)
      result <- merge.run(
                 uniqueTag,
                 dataCh0,
                 dataCh1,
                 dataState
               )
    } yield result
  }

  private final def onChangeNonEmpty(
      ch0: SignalChannelCreated,
      ch1: SignalChannelCreated,
      state: ChannelCreated
  ): ZIO[Any, String, AggregateResult[Output]] = {
    for {
      ch0Producer <- askReadyProducer[Id0](ch0.producer, channelId0, channelTag0)
      ch1Producer <- askReadyProducer[Id1](ch1.producer, channelId1, channelTag1)
      dataCh0     <- askReadyNonEmptyData[Input0](ch0Producer, channelId0, channelTag0)
      dataCh1     <- askReadyNonEmptyData[Input1](ch1Producer, channelId1, channelTag1)
      dataState   <- askDataIfNotRunning(state.producer, channelIdState, channelTagState)
      _ <- ZIO
            .effectTotal(
              log.info(
                s"$uniqueTag. on-change. ch-0: ${dataCh0.length}. ch-1: ${dataCh1.length}. state: ${dataState.length}."
              )
            )
            .when(enableTrace)
      result <- merge.run(
                 uniqueTag,
                 dataCh0,
                 dataCh1,
                 dataState
               )
    } yield result
  }

  protected final def onChange(
      ch0: SignalChannelCreated,
      ch1: SignalChannelCreated,
      state: ChannelCreated
  ): Unit = {
    cancelDrainTimer()
    val effect = if (channelMayBeEmpty) {
      onChangeMayBeEmpty(ch0, ch1, state)
    } else {
      onChangeNonEmpty(ch0, ch1, state)
    }
    zioRuntime.unsafeRunAsync(effect)(
      cbArrow(self, uniqueTag, state.producer, state.channelId, startDrainTimer, log)
    )
  }

  protected final def shutdown(): Unit = {
    log.info(s"$uniqueTag. shutdown")
    context.stop(self)
  }
}
