package bookingtour.core.actors.primitives.transforms.two

import akka.actor.{Actor, ActorLogging}
import bookingtour.core.actors.primitives.transforms.{CheckDrain, TransformCompleted, TransformFailed}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.{ChannelCreate, ChannelPushStatus}
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.SignalChannelCreate
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.{
  SignalChannelCreated,
  SignalChannelStatusChanged
}
import cats.instances.uuid._
import cats.syntax.eq._

/**
  * © Alexey Toroshchin 2019.
  */
protected[two] trait BasicBehavior[Input0, Id0, Input1, Id1, Output, OutputId] {
  _: Actor with ActorLogging with State[Input0, Id0, Input1, Id1, Output, OutputId] =>

  private final def behaviors(
      ch0: SignalChannelCreated,
      ch0Status: ChannelStatus,
      ch1: SignalChannelCreated,
      ch1Status: ChannelStatus,
      state: ChannelCreated,
      arrowRun: Boolean,
      needDrain: Boolean
  ): Receive = {
    case msg: ChannelCreate =>
      state.producer.forward(msg)

    case msg: SignalChannelCreate =>
      state.producer.forward(msg)

    case SignalChannelStatusChanged(id, s) if id === ch0.channelId =>
      cancelDrainTimer()
      val sum                  = calculateStatus(s, ch1Status)
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 0. signal-channel-status-changed received. 0-value: $s. 1-value: $ch1Status. status sum: $sum. runnable: $runnable."
        )
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = s,
        ch1 = ch1,
        ch1Status = ch1Status,
        state = state,
        arrowRun = runnable,
        needDrain = drain
      )
      if (runnable) {
        state.producer ! ChannelPushStatus(state.channelId, ChannelStatus.Busy)
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch1.channelId =>
      cancelDrainTimer()
      val sum                  = calculateStatus(ch0Status, s)
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 1. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $s. status sum: $sum. runnable: $runnable."
        )
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = ch0Status,
        ch1 = ch1,
        ch1Status = s,
        state = state,
        arrowRun = runnable,
        needDrain = drain
      )
      if (runnable) {
        state.producer ! ChannelPushStatus(state.channelId, ChannelStatus.Busy)
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case CheckDrain =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. check-drain received."
        )
      }
      if (!arrowRun && needDrain && calculateStatus(ch0Status, ch1Status) === ChannelStatus.Ready) {
        basicBehavior(
          ch0 = ch0,
          ch0Status = ch0Status,
          ch1 = ch1,
          ch1Status = ch1Status,
          state = state,
          arrowRun = true,
          needDrain = false
        )
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          state = state
        )
      }

    case TransformFailed =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. transform-failed received."
        )
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = ch0Status,
        ch1 = ch1,
        ch1Status = ch1Status,
        state = state,
        arrowRun = false,
        needDrain = true
      )

    case TransformCompleted =>
      val sum = calculateStatus(ch0Status, ch1Status)
      if (enableTrace) {
        log.info(
          s"$uniqueTag. transform-completed received. status sum: $sum. need-drain: $needDrain."
        )
      }
      if (needDrain && sum === ChannelStatus.Ready) {
        basicBehavior(
          ch0 = ch0,
          ch0Status = ch0Status,
          ch1 = ch1,
          ch1Status = ch1Status,
          state = state,
          arrowRun = true,
          needDrain = false
        )
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          state = state
        )
      } else {
        state.producer ! ChannelPushStatus(state.channelId, sum)
        basicBehavior(
          ch0 = ch0,
          ch0Status = ch0Status,
          ch1 = ch1,
          ch1Status = ch1Status,
          state = state,
          arrowRun = false,
          needDrain = needDrain
        )
      }

    case msg =>
      log.error(s"$uniqueTag. basic-behavior. unhandled $msg")
      shutdown()
  }

  protected final def basicBehavior(
      ch0: SignalChannelCreated,
      ch0Status: ChannelStatus,
      ch1: SignalChannelCreated,
      ch1Status: ChannelStatus,
      state: ChannelCreated,
      arrowRun: Boolean,
      needDrain: Boolean
  ): Unit = context.become(
    behaviors(
      ch0 = ch0,
      ch0Status = ch0Status,
      ch1 = ch1,
      ch1Status = ch1Status,
      state = state,
      arrowRun = arrowRun,
      needDrain = needDrain
    )
  )
}
