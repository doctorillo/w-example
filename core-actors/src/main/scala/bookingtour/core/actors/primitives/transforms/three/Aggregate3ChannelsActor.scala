package bookingtour.core.actors.primitives.transforms.three

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Timers}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.aggregators.{Aggregate3Channels, Aggregate3Fn}
import bookingtour.protocols.actors.channels.{MakeChannel, MakeSignalChannel}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated
import cats.{Order, Semigroup}
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class Aggregate3ChannelsActor[Input0, Id0, Input1, Id1, Input2, Id2, Output, OutputId] private (
    val uniqueTag: String,
    val makeChannel0: MakeSignalChannel,
    val makeChannel1: MakeSignalChannel,
    val makeChannel2: MakeSignalChannel,
    val makeChannelState: MakeChannel[Output, OutputId],
    val merge: Aggregate3Channels[Input0, Input1, Input2, Output],
    val channelMayBeEmpty: Boolean,
    val enableTrace: Boolean
)(
    implicit val statusSG: Semigroup[ChannelStatus],
    val zioRuntime: zio.Runtime[zio.ZEnv],
    val statusO: Order[ChannelStatus]
) extends Actor with Timers with ActorLogging with State[Input0, Id0, Input1, Id1, Input2, Id2, Output, OutputId]
    with BasicBehavior[Input0, Id0, Input1, Id1, Input2, Id2, Output, OutputId] {
  override def preStart(): Unit = {
    super.preStart()
    val ch0Ref = makeChannel0.make[Id0](context, self, channelTag0, channelId0)
    val ch0 = SignalChannelCreated(
      channelId = channelId0,
      tag = channelTag0,
      producer = ch0Ref.x,
      consumer = self
    )
    if (enableTrace) {
      log.info(s"$uniqueTag. tag: ${ch0.tag}. id: ${ch0.channelId}")
    }
    val ch1Ref = makeChannel1.make[Id1](context, self, channelTag1, channelId1)
    val ch1 = SignalChannelCreated(
      channelId = channelId1,
      tag = channelTag1,
      producer = ch1Ref.x,
      consumer = self
    )
    if (enableTrace) {
      log.info(s"$uniqueTag. tag: ${ch1.tag}. id: ${ch1.channelId}")
    }
    val ch2Ref = makeChannel2.make[Id2](context, self, channelTag2, channelId2)
    val ch2 = SignalChannelCreated(
      channelId = channelId2,
      tag = channelTag2,
      producer = ch2Ref.x,
      consumer = self
    )
    if (enableTrace) {
      log.info(s"$uniqueTag. tag: ${ch2.tag}. id: ${ch2.channelId}")
    }
    val chStateRef = makeChannelState.make(context, self, channelIdState, channelTagState)
    val state = ChannelCreated(
      channelId = channelIdState,
      tag = channelTagState,
      producer = chStateRef.x,
      consumer = self
    )
    if (enableTrace) {
      log.info(s"$uniqueTag. tag: ${state.tag}. id: ${state.channelId}")
    }
    basicBehavior(
      ch0 = ch0,
      ch0Status = ChannelStatus.Undefined,
      ch1 = ch1,
      ch1Status = ChannelStatus.Undefined,
      ch2 = ch2,
      ch2Status = ChannelStatus.Undefined,
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

object Aggregate3ChannelsActor {
  final def make[Input0, Id0, Input1, Id1, Input2, Id2, Output, OutputId](
      uniqueTag: String,
      makeChannel0: MakeSignalChannel,
      makeChannel1: MakeSignalChannel,
      makeChannel2: MakeSignalChannel,
      makeChannelState: MakeChannel[Output, OutputId],
      channelMayBeEmpty: Boolean,
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      mergeFn: Aggregate3Fn[Input0, Input1, Input2, Output],
      statusSG: Semigroup[ChannelStatus],
      statusO: Order[ChannelStatus],
      o0: Order[Output],
      o1: Order[OutputId],
      r0: Output => OutputId
  ): ActorProducer[Output, OutputId] =
    ctx
      .actorOf(
        Props(
          new Aggregate3ChannelsActor(
            uniqueTag = uniqueTag,
            makeChannel0 = makeChannel0,
            makeChannel1 = makeChannel1,
            makeChannel2 = makeChannel2,
            makeChannelState = makeChannelState,
            merge = Aggregate3Channels.make[Input0, Input1, Input2, Output, OutputId],
            channelMayBeEmpty = channelMayBeEmpty,
            enableTrace = enableTrace
          )
        )
      )
      .coerce[ActorProducer[Output, OutputId]]
}
