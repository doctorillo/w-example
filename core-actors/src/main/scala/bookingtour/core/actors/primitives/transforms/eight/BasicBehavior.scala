package bookingtour.core.actors.primitives.transforms.eight

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
protected[eight] trait BasicBehavior[
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
    Input5,
    Id5,
    Input6,
    Id6,
    Input7,
    Id7,
    Output,
    OutputId
] {
  _: Actor
    with ActorLogging with State[
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
      Input5,
      Id5,
      Input6,
      Id6,
      Input7,
      Id7,
      Output,
      OutputId
    ] =>

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
      ch5: SignalChannelCreated,
      ch5Status: ChannelStatus,
      ch6: SignalChannelCreated,
      ch6Status: ChannelStatus,
      ch7: SignalChannelCreated,
      ch7Status: ChannelStatus,
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
      val sum = calculateStatus(
        s,
        ch1Status,
        ch2Status,
        ch3Status,
        ch4Status,
        ch5Status,
        ch6Status,
        ch7Status
      )
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 0. signal-channel-status-changed received. 0-value: $s. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $ch4Status. 5-value: $ch5Status. 6-value: $ch6Status. 7-value: $ch7Status. status sum: $sum. runnable: $runnable."
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
        ch5 = ch5,
        ch5Status = ch5Status,
        ch6 = ch6,
        ch6Status = ch6Status,
        ch7 = ch7,
        ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch1.channelId =>
      cancelDrainTimer()
      val sum = calculateStatus(
        ch0Status,
        s,
        ch2Status,
        ch3Status,
        ch4Status,
        ch5Status,
        ch6Status,
        ch7Status
      )
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 1. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $s. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $ch4Status. 5-value: $ch5Status. 6-value: $ch6Status. 7-value: $ch7Status. status sum: $sum. runnable: $runnable."
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
        ch5 = ch5,
        ch5Status = ch5Status,
        ch6 = ch6,
        ch6Status = ch6Status,
        ch7 = ch7,
        ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch2.channelId =>
      cancelDrainTimer()
      val sum = calculateStatus(
        ch0Status,
        ch1Status,
        s,
        ch3Status,
        ch4Status,
        ch5Status,
        ch6Status,
        ch7Status
      )
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 2. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $s. 3-value: $ch3Status. 4-value: $ch4Status. 5-value: $ch5Status. 6-value: $ch6Status. 7-value: $ch7Status. status sum: $sum. runnable: $runnable."
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
        ch5 = ch5,
        ch5Status = ch5Status,
        ch6 = ch6,
        ch6Status = ch6Status,
        ch7 = ch7,
        ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch3.channelId =>
      cancelDrainTimer()
      val sum = calculateStatus(
        ch0Status,
        ch1Status,
        ch2Status,
        s,
        ch4Status,
        ch5Status,
        ch6Status,
        ch7Status
      )
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 3. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $s. 4-value: $ch4Status. 5-value: $ch5Status. 6-value: $ch6Status. 7-value: $ch7Status. status sum: $sum. runnable: $runnable."
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
        ch5 = ch5,
        ch5Status = ch5Status,
        ch6 = ch6,
        ch6Status = ch6Status,
        ch7 = ch7,
        ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch4.channelId =>
      cancelDrainTimer()
      val sum = calculateStatus(
        ch0Status,
        ch1Status,
        ch2Status,
        ch3Status,
        s,
        ch5Status,
        ch6Status,
        ch7Status
      )
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 4. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $s. 5-value: $ch5Status. 6-value: $ch6Status. 7-value: $ch7Status. status sum: $sum. runnable: $runnable."
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
        ch5 = ch5,
        ch5Status = ch5Status,
        ch6 = ch6,
        ch6Status = ch6Status,
        ch7 = ch7,
        ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch5.channelId =>
      cancelDrainTimer()
      val sum = calculateStatus(
        ch0Status,
        ch1Status,
        ch2Status,
        ch3Status,
        ch4Status,
        s,
        ch6Status,
        ch7Status
      )
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 5. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $ch4Status. 5-value: $s. 6-value: $ch6Status. 7-value: $ch7Status. status sum: $sum. runnable: $runnable."
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
        ch4Status = ch4Status,
        ch5 = ch5,
        ch5Status = s,
        ch6 = ch6,
        ch6Status = ch6Status,
        ch7 = ch7,
        ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch6.channelId =>
      cancelDrainTimer()
      val sum = calculateStatus(
        ch0Status,
        ch1Status,
        ch2Status,
        ch3Status,
        ch4Status,
        ch5Status,
        s,
        ch7Status
      )
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 5. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $ch4Status. 5-value: $ch5Status. 6-value: $s. 7-value: $ch7Status. status sum: $sum. runnable: $runnable."
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
        ch4Status = ch4Status,
        ch5 = ch5,
        ch5Status = ch5Status,
        ch6 = ch6,
        ch6Status = s,
        ch7 = ch7,
        ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
          state = state
        )
      } else if (sum =!= ChannelStatus.Ready) {
        state.producer ! ChannelPushStatus(state.channelId, sum)
      }

    case SignalChannelStatusChanged(id, s) if id === ch7.channelId =>
      cancelDrainTimer()
      val sum = calculateStatus(
        ch0Status,
        ch1Status,
        ch2Status,
        ch3Status,
        ch4Status,
        ch5Status,
        ch6Status,
        s
      )
      val runnable             = !arrowRun && sum === ChannelStatus.Ready
      val drainAfterMergeStart = arrowRun && sum === ChannelStatus.Ready
      val resetDrain           = runnable && needDrain
      val drain                = drainAfterMergeStart || !resetDrain
      if (enableTrace) {
        log.info(
          s"$uniqueTag. basic-behavior. channel: 5. signal-channel-status-changed received. 0-value: $ch0Status. 1-value: $ch1Status. 2-value: $ch2Status. 3-value: $ch3Status. 4-value: $ch4Status. 5-value: $ch5Status. 6-value: $ch6Status. 7-value: $s. status sum: $sum. runnable: $runnable."
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
        ch4Status = ch4Status,
        ch5 = ch5,
        ch5Status = ch5Status,
        ch6 = ch6,
        ch6Status = ch6Status,
        ch7 = ch7,
        ch7Status = s,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
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
            ch4Status,
            ch5Status,
            ch6Status,
            ch7Status
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
          ch5 = ch5,
          ch5Status = ch5Status,
          ch6 = ch6,
          ch6Status = ch6Status,
          ch7 = ch7,
          ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
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
        ch5 = ch5,
        ch5Status = ch5Status,
        ch6 = ch6,
        ch6Status = ch6Status,
        ch7 = ch7,
        ch7Status = ch7Status,
        state = state,
        arrowRun = false,
        needDrain = true
      )

    case TransformCompleted =>
      val sum =
        calculateStatus(
          ch0Status,
          ch1Status,
          ch2Status,
          ch3Status,
          ch4Status,
          ch5Status,
          ch6Status,
          ch7Status
        )
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
          ch5 = ch5,
          ch5Status = ch5Status,
          ch6 = ch6,
          ch6Status = ch6Status,
          ch7 = ch7,
          ch7Status = ch7Status,
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
          ch5 = ch5,
          ch6 = ch6,
          ch7 = ch7,
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
          ch5 = ch5,
          ch5Status = ch5Status,
          ch6 = ch6,
          ch6Status = ch6Status,
          ch7 = ch7,
          ch7Status = ch7Status,
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
      ch5: SignalChannelCreated,
      ch5Status: ChannelStatus,
      ch6: SignalChannelCreated,
      ch6Status: ChannelStatus,
      ch7: SignalChannelCreated,
      ch7Status: ChannelStatus,
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
      ch5 = ch5,
      ch5Status = ch5Status,
      ch6 = ch6,
      ch6Status = ch6Status,
      ch7 = ch7,
      ch7Status = ch7Status,
      state = state,
      arrowRun = arrowRun,
      needDrain = needDrain
    )
  )
}
