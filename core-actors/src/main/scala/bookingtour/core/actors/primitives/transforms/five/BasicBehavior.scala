package bookingtour.core.actors.primitives.transforms.five

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
protected[five] trait BasicBehavior[IN0, ID0, IN1, ID1, IN2, ID2, IN3, ID3, IN4, ID4, OUT, OUT_ID] {
  _: Actor with ActorLogging with State[IN0, ID0, IN1, ID1, IN2, ID2, IN3, ID3, IN4, ID4, OUT, OUT_ID] =>

  private final def behaviors(
      ch0: SignalChannelCreated,
      ch0Status: ChannelStatus,
      ch1: SignalChannelCreated,
      ch1Status: ChannelStatus,
      ch2: SignalChannelCreated,
      ch2Status: ChannelStatus,
      ch3: SignalChannelCreated,
      ch3Status: ChannelStatus,
      ch4: SignalChannelCreated,
      ch4Status: ChannelStatus,
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
      val sum                  = calculateStatus(s, ch1Status, ch2Status, ch3Status, ch4Status)
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 0. signal-channel-status-changed received. 0-value: $s. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $ch4Status. status sum: $sum. runnable: $runnable."
        )
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = s,
        ch1 = ch1,
        ch1Status = ch1Status,
        ch2 = ch2,
        ch2Status = ch2Status,
        ch3 = ch3,
        ch3Status = ch3Status,
        ch4 = ch4,
        ch4Status = ch4Status,
        state = state,
        arrowRun = runnable,
        needDrain = drain
      )
      if (runnable) {
        state.producer ! ChannelPushStatus(state.channelId, ChannelStatus.Busy)
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          ch2 = ch2,
          ch3 = ch3,
          ch4 = ch4,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch1.channelId =>
      cancelDrainTimer()
      val sum                  = calculateStatus(ch0Status, s, ch2Status, ch3Status, ch4Status)
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 1. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $s. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $ch4Status. status sum: $sum. runnable: $runnable."
        )
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = ch0Status,
        ch1 = ch1,
        ch1Status = s,
        ch2 = ch2,
        ch2Status = ch2Status,
        ch3 = ch3,
        ch3Status = ch3Status,
        ch4 = ch4,
        ch4Status = ch4Status,
        state = state,
        arrowRun = runnable,
        needDrain = drain
      )
      if (runnable) {
        state.producer ! ChannelPushStatus(state.channelId, ChannelStatus.Busy)
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          ch2 = ch2,
          ch3 = ch3,
          ch4 = ch4,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch2.channelId =>
      cancelDrainTimer()
      val sum                  = calculateStatus(ch0Status, ch1Status, s, ch3Status, ch4Status)
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 2. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $s. 3-value: $ch3Status. 4-value: $ch4Status. status sum: $sum. runnable: $runnable."
        )
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = ch0Status,
        ch1 = ch1,
        ch1Status = ch1Status,
        ch2 = ch2,
        ch2Status = s,
        ch3 = ch3,
        ch3Status = ch3Status,
        ch4 = ch4,
        ch4Status = ch4Status,
        state = state,
        arrowRun = runnable,
        needDrain = drain
      )
      if (runnable) {
        state.producer ! ChannelPushStatus(state.channelId, ChannelStatus.Busy)
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          ch2 = ch2,
          ch3 = ch3,
          ch4 = ch4,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch3.channelId =>
      cancelDrainTimer()
      val sum                  = calculateStatus(ch0Status, ch1Status, ch2Status, s, ch4Status)
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 3. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $s. 4-value: $ch4Status. status sum: $sum. runnable: $runnable."
        )
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = ch0Status,
        ch1 = ch1,
        ch1Status = ch1Status,
        ch2 = ch2,
        ch2Status = ch2Status,
        ch3 = ch3,
        ch3Status = s,
        ch4 = ch4,
        ch4Status = ch4Status,
        state = state,
        arrowRun = runnable,
        needDrain = drain
      )
      if (runnable) {
        state.producer ! ChannelPushStatus(state.channelId, ChannelStatus.Busy)
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          ch2 = ch2,
          ch3 = ch3,
          ch4 = ch4,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch4.channelId =>
      cancelDrainTimer()
      val sum                  = calculateStatus(ch0Status, ch1Status, ch2Status, ch3Status, s)
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 4. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $s. status sum: $sum. runnable: $runnable."
        )
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = ch0Status,
        ch1 = ch1,
        ch1Status = ch1Status,
        ch2 = ch2,
        ch2Status = ch2Status,
        ch3 = ch3,
        ch3Status = ch3Status,
        ch4 = ch4,
        ch4Status = s,
        state = state,
        arrowRun = runnable,
        needDrain = drain
      )
      if (runnable) {
        state.producer ! ChannelPushStatus(state.channelId, ChannelStatus.Busy)
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          ch2 = ch2,
          ch3 = ch3,
          ch4 = ch4,
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
      if (!arrowRun && needDrain && calculateStatus(
            ch0Status,
            ch1Status,
            ch2Status,
            ch3Status,
            ch4Status
          ) === ChannelStatus.Ready) {
        basicBehavior(
          ch0 = ch0,
          ch0Status = ch0Status,
          ch1 = ch1,
          ch1Status = ch1Status,
          ch2 = ch2,
          ch2Status = ch2Status,
          ch3 = ch3,
          ch3Status = ch3Status,
          ch4 = ch4,
          ch4Status = ch4Status,
          state = state,
          arrowRun = true,
          needDrain = false
        )
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          ch2 = ch2,
          ch3 = ch3,
          ch4 = ch4,
          state = state
        )
      }

    case TransformFailed =>
      if (enableTrace) {
        log.info(s"$uniqueTag. transform-failed received.")
      }
      basicBehavior(
        ch0 = ch0,
        ch0Status = ch0Status,
        ch1 = ch1,
        ch1Status = ch1Status,
        ch2 = ch2,
        ch2Status = ch2Status,
        ch3 = ch3,
        ch3Status = ch3Status,
        ch4 = ch4,
        ch4Status = ch4Status,
        state = state,
        arrowRun = false,
        needDrain = true
      )

    case TransformCompleted =>
      if (enableTrace) {
        log.info(s"$uniqueTag. transform-completed received.")
      }
      val sum = calculateStatus(ch0Status, ch1Status, ch2Status, ch3Status, ch4Status)
      if (needDrain && sum === ChannelStatus.Ready) {
        basicBehavior(
          ch0 = ch0,
          ch0Status = ch0Status,
          ch1 = ch1,
          ch1Status = ch1Status,
          ch2 = ch2,
          ch2Status = ch2Status,
          ch3 = ch3,
          ch3Status = ch3Status,
          ch4 = ch4,
          ch4Status = ch4Status,
          state = state,
          arrowRun = true,
          needDrain = false
        )
        onChange(
          ch0 = ch0,
          ch1 = ch1,
          ch2 = ch2,
          ch3 = ch3,
          ch4 = ch4,
          state = state
        )
      } else {
        state.producer ! ChannelPushStatus(state.channelId, sum)
        basicBehavior(
          ch0 = ch0,
          ch0Status = ch0Status,
          ch1 = ch1,
          ch1Status = ch1Status,
          ch2 = ch2,
          ch2Status = ch2Status,
          ch3 = ch3,
          ch3Status = ch3Status,
          ch4 = ch4,
          ch4Status = ch4Status,
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
      ch2: SignalChannelCreated,
      ch2Status: ChannelStatus,
      ch3: SignalChannelCreated,
      ch3Status: ChannelStatus,
      ch4: SignalChannelCreated,
      ch4Status: ChannelStatus,
      state: ChannelCreated,
      arrowRun: Boolean,
      needDrain: Boolean
  ): Unit = context.become(
    behaviors(
      ch0 = ch0,
      ch0Status = ch0Status,
      ch1 = ch1,
      ch1Status = ch1Status,
      ch2 = ch2,
      ch2Status = ch2Status,
      ch3 = ch3,
      ch3Status = ch3Status,
      ch4 = ch4,
      ch4Status = ch4Status,
      state = state,
      arrowRun = arrowRun,
      needDrain = needDrain
    )
  )
}
