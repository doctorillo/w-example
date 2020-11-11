package bookingtour.core.actors.primitives.transforms.five

import java.util.UUID
import java.util.concurrent.TimeUnit

import scala.concurrent.duration.FiniteDuration

import akka.actor.{Actor, ActorLogging, Timers}
import bookingtour.core.actors.primitives.transforms.{cbArrow, CheckDrain}
import bookingtour.protocols.actors.aggregators.{Aggregate5Channels, AggregateResult}
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
protected[five] trait State[
    Input0,
    Id0,
    Input1,
    Id1,
    Input2,
    Id2,
    Input3,
    Id3,
    Input4,
    Id4,
    Output,
    OutputId
] {
  _: Actor with Timers with ActorLogging =>

  protected val uniqueTag: String
  protected val makeChannel0: MakeSignalChannel
  protected val makeChannel1: MakeSignalChannel
  protected val makeChannel2: MakeSignalChannel
  protected val makeChannel3: MakeSignalChannel
  protected val makeChannel4: MakeSignalChannel
  protected val makeChannelState: MakeChannel[Output, OutputId]
  protected val merge: Aggregate5Channels[Input0, Input1, Input2, Input3, Input4, Output]
  protected val channelMayBeEmpty: Boolean
  protected val enableTrace: Boolean

  val zioRuntime: zio.Runtime[zio.ZEnv]

  implicit val statusO: Order[ChannelStatus]
  implicit val statusSG: Semigroup[ChannelStatus]

  protected final val channelId0: UUID     = UUID.randomUUID()
  protected final def channelTag0: String  = s"$uniqueTag:ch-0"
  protected final val channelId1: UUID     = UUID.randomUUID()
  protected final def channelTag1: String  = s"$uniqueTag:ch-1"
  protected final val channelId2: UUID     = UUID.randomUUID()
  protected final def channelTag2: String  = s"$uniqueTag:ch-2"
  protected final val channelId3: UUID     = UUID.randomUUID()
  protected final def channelTag3: String  = s"$uniqueTag:ch-3"
  protected final val channelId4: UUID     = UUID.randomUUID()
  protected final def channelTag4: String  = s"$uniqueTag:ch-4"
  protected final val channelIdState: UUID = UUID.randomUUID()

  protected final def channelTagState: String = s"$uniqueTag:state"

  private final val drainTimerKey = UUID.randomUUID()
  private final val drainDuration = FiniteDuration(30L, TimeUnit.SECONDS)

  protected final def cancelDrainTimer(): Unit = {
    if (timers.isTimerActive(drainTimerKey)) {
      timers.cancel(drainTimerKey)
    }
  }

  private final def startDrainTimer(): Unit =
    timers.startSingleTimer(drainTimerKey, CheckDrain, drainDuration)

  protected def calculateStatus(
      ch0: ChannelStatus,
      ch1: ChannelStatus,
      ch2: ChannelStatus,
      ch3: ChannelStatus,
      ch4: ChannelStatus
  ): ChannelStatus = {
    if (channelMayBeEmpty) {
      ch0
    } else {
      ch0 |+| ch1 |+| ch2 |+| ch3 |+| ch4
    }
  }

  private final def onChangeMayBeEmpty(
      ch0: SignalChannelCreated,
      ch1: SignalChannelCreated,
      ch2: SignalChannelCreated,
      ch3: SignalChannelCreated,
      ch4: SignalChannelCreated,
      state: ChannelCreated
  ): ZIO[Any, String, AggregateResult[Output]] = {
    for {
      ch0Producer <- askProducer[Id0](ch0.producer, channelId0, channelTag0)
      ch1Producer <- askProducer[Id1](ch1.producer, channelId1, channelTag1)
      ch2Producer <- askProducer[Id2](ch2.producer, channelId2, channelTag2)
      ch3Producer <- askProducer[Id3](ch3.producer, channelId3, channelTag3)
      ch4Producer <- askProducer[Id4](ch4.producer, channelId4, channelTag4)
      dataCh0     <- askDataIfNotRunning[Input0](ch0Producer, channelId0, channelTag0)
      dataCh1     <- askDataIfNotRunning[Input1](ch1Producer, channelId1, channelTag1)
      dataCh2     <- askDataIfNotRunning[Input2](ch2Producer, channelId2, channelTag2)
      dataCh3     <- askDataIfNotRunning[Input3](ch3Producer, channelId3, channelTag3)
      dataCh4     <- askDataIfNotRunning[Input4](ch4Producer, channelId4, channelTag4)
      dataState   <- askDataIfNotRunning(state.producer, channelIdState, channelTagState)
      _ <- ZIO
            .effectTotal(
              log.info(
                s"$uniqueTag. on-change. ch-0: ${dataCh0.length}. ch-1: ${dataCh1.length}. ch-2: ${dataCh2.length}. ch-3: ${dataCh3.length}. ch-4: ${dataCh4.length}. state: ${dataState.length}."
              )
            )
            .when(enableTrace)
      result <- merge.run(
                 uniqueTag,
                 dataCh0,
                 dataCh1,
                 dataCh2,
                 dataCh3,
                 dataCh4,
                 dataState
               )
    } yield result
  }

  private final def onChangeNonEmpty(
      ch0: SignalChannelCreated,
      ch1: SignalChannelCreated,
      ch2: SignalChannelCreated,
      ch3: SignalChannelCreated,
      ch4: SignalChannelCreated,
      state: ChannelCreated
  ): ZIO[Any, String, AggregateResult[Output]] = {
    for {
      ch0Producer <- askReadyProducer[Id0](ch0.producer, channelId0, channelTag0)
      ch1Producer <- askReadyProducer[Id1](ch1.producer, channelId1, channelTag1)
      ch2Producer <- askReadyProducer[Id2](ch2.producer, channelId2, channelTag2)
      ch3Producer <- askReadyProducer[Id3](ch3.producer, channelId3, channelTag3)
      ch4Producer <- askReadyProducer[Id4](ch4.producer, channelId4, channelTag4)
      dataCh0     <- askReadyNonEmptyData[Input0](ch0Producer, channelId0, channelTag0)
      dataCh1     <- askReadyNonEmptyData[Input1](ch1Producer, channelId1, channelTag1)
      dataCh2     <- askReadyNonEmptyData[Input2](ch2Producer, channelId2, channelTag2)
      dataCh3     <- askReadyNonEmptyData[Input3](ch3Producer, channelId3, channelTag3)
      dataCh4     <- askReadyNonEmptyData[Input4](ch4Producer, channelId4, channelTag4)
      dataState   <- askDataIfNotRunning(state.producer, channelIdState, channelTagState)
      _ <- ZIO
            .effectTotal(
              log.info(
                s"$uniqueTag. on-change. ch-0: ${dataCh0.length}. ch-1: ${dataCh1.length}. ch-2: ${dataCh2.length}. ch-3: ${dataCh3.length}. ch-4: ${dataCh4.length}. state: ${dataState.length}."
              )
            )
            .when(enableTrace)
      result <- merge.run(
                 uniqueTag,
                 dataCh0,
                 dataCh1,
                 dataCh2,
                 dataCh3,
                 dataCh4,
                 dataState
               )
    } yield result
  }

  protected final def onChange(
      ch0: SignalChannelCreated,
      ch1: SignalChannelCreated,
      ch2: SignalChannelCreated,
      ch3: SignalChannelCreated,
      ch4: SignalChannelCreated,
      state: ChannelCreated
  ): Unit = {
    cancelDrainTimer()
    val effect = if (channelMayBeEmpty) {
      onChangeMayBeEmpty(ch0, ch1, ch2, ch3, ch4, state)
    } else {
      onChangeNonEmpty(ch0, ch1, ch2, ch3, ch4, state)
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
