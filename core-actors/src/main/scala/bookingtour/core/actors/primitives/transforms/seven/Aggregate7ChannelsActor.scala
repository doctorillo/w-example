package bookingtour.core.actors.primitives.transforms.seven

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Timers}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.aggregators.{Aggregate7Channels, Aggregate7Fn}
import bookingtour.protocols.actors.channels.{MakeChannel, MakeSignalChannel}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated
import cats.{Order, Semigroup}

/**
  * © Alexey Toroshchin 2020.
  */
final class Aggregate7ChannelsActor[
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
    Output,
    OutputId
] private (
    val uniqueTag: String,
    val makeChannel0: MakeSignalChannel,
    val makeChannel1: MakeSignalChannel,
    val makeChannel2: MakeSignalChannel,
    val makeChannel3: MakeSignalChannel,
    val makeChannel4: MakeSignalChannel,
    val makeChannel5: MakeSignalChannel,
    val makeChannel6: MakeSignalChannel,
    val makeChannelState: MakeChannel[Output, OutputId],
    val merge: Aggregate7Channels[Input0, Input1, Input2, Input3, Input4, Input5, Input6, Output],
    val channelMayBeEmpty: Boolean,
    val enableTrace: Boolean
)(
    implicit val zioRuntime: zio.Runtime[zio.ZEnv],
    val statusSG: Semigroup[ChannelStatus],
    val statusO: Order[ChannelStatus]
) extends Actor with Timers with ActorLogging with State[
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
      Output,
      OutputId
    ] with BasicBehavior[
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
      Output,
      OutputId
    ] {
  override def preStart(): Unit = {
    super.preStart()
    val ch0Ref = makeChannel0.make[Id0](context, self, channelTag0, channelId0)
    val ch0 = SignalChannelCreated(
      channelId = channelId0,
      tag = channelTag0,
      producer = ch0Ref.x,
      consumer = self
    )
    val ch1Ref = makeChannel1.make[Id1](context, self, channelTag1, channelId1)
    val ch1 = SignalChannelCreated(
      channelId = channelId1,
      tag = channelTag1,
      producer = ch1Ref.x,
      consumer = self
    )
    val ch2Ref = makeChannel2.make[Id2](context, self, channelTag2, channelId2)
    val ch2 = SignalChannelCreated(
      channelId = channelId2,
      tag = channelTag2,
      producer = ch2Ref.x,
      consumer = self
    )
    val ch3Ref = makeChannel3.make[Id3](context, self, channelTag3, channelId3)
    val ch3 = SignalChannelCreated(
      channelId = channelId3,
      tag = channelTag3,
      producer = ch3Ref.x,
      consumer = self
    )
    val ch4Ref = makeChannel4.make[Id4](context, self, channelTag4, channelId4)
    val ch4 = SignalChannelCreated(
      channelId = channelId4,
      tag = channelTag4,
      producer = ch4Ref.x,
      consumer = self
    )
    val ch5Ref = makeChannel5.make[Id5](context, self, channelTag5, channelId5)
    val ch5 = SignalChannelCreated(
      channelId = channelId5,
      tag = channelTag5,
      producer = ch5Ref.x,
      consumer = self
    )
    val ch6Ref = makeChannel6.make[Id6](context, self, channelTag6, channelId6)
    val ch6 = SignalChannelCreated(
      channelId = channelId6,
      tag = channelTag6,
      producer = ch6Ref.x,
      consumer = self
    )
    val chStateRef = makeChannelState.make(context, self, channelIdState, channelTagState)
    val state = ChannelCreated(
      channelId = channelIdState,
      tag = channelTagState,
      producer = chStateRef.x,
      consumer = self
    )
    basicBehavior(
      ch0 = ch0,
      ch0Status = ChannelStatus.Undefined,
      ch1 = ch1,
      ch1Status = ChannelStatus.Undefined,
      ch2 = ch2,
      ch2Status = ChannelStatus.Undefined,
      ch3 = ch3,
      ch3Status = ChannelStatus.Undefined,
      ch4 = ch4,
      ch4Status = ChannelStatus.Undefined,
      ch5 = ch5,
      ch5Status = ChannelStatus.Undefined,
      ch6 = ch6,
      ch6Status = ChannelStatus.Undefined,
      state = state,
      arrowRun = false,
      needDrain = false
    )
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.error(s"$uniqueTag. post-restart. {}", reason)
    shutdown()
  }

  def receive: Receive = Actor.emptyBehavior
}

object Aggregate7ChannelsActor {
  final def make[
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
      Output,
      OutputId
  ](
      uniqueTag: String,
      makeChannel0: MakeSignalChannel,
      makeChannel1: MakeSignalChannel,
      makeChannel2: MakeSignalChannel,
      makeChannel3: MakeSignalChannel,
      makeChannel4: MakeSignalChannel,
      makeChannel5: MakeSignalChannel,
      makeChannel6: MakeSignalChannel,
      makeChannelState: MakeChannel[Output, OutputId],
      channelMayBeEmpty: Boolean,
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      mergeFn: Aggregate7Fn[Input0, Input1, Input2, Input3, Input4, Input5, Input6, Output],
      statusSG: Semigroup[ChannelStatus],
      statusO: Order[ChannelStatus],
      o0: Order[Output],
      o1: Order[OutputId],
      r0: Output => OutputId
  ): ActorProducer[Output, OutputId] =
    ActorProducer[Output, OutputId](
      ctx
        .actorOf(
          Props(
            new Aggregate7ChannelsActor(
              uniqueTag = uniqueTag,
              makeChannel0 = makeChannel0,
              makeChannel1 = makeChannel1,
              makeChannel2 = makeChannel2,
              makeChannel3 = makeChannel3,
              makeChannel4 = makeChannel4,
              makeChannel5 = makeChannel5,
              makeChannel6 = makeChannel6,
              makeChannelState = makeChannelState,
              merge = Aggregate7Channels
                .make[Input0, Input1, Input2, Input3, Input4, Input5, Input6, Output, OutputId],
              channelMayBeEmpty = channelMayBeEmpty,
              enableTrace = enableTrace
            )
          )
        )
    )
}
