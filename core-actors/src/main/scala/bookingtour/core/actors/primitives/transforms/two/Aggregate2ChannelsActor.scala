package bookingtour.core.actors.primitives.transforms.two

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash, Timers}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.aggregators.{Aggregate2Channels, Aggregate2Fn}
import bookingtour.protocols.actors.channels.{MakeChannel, MakeSignalChannel}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.{ChannelCreate, ChannelDelete}
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.{SignalChannelCreate, SignalChannelDelete}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated
import bookingtour.protocols.core.actors.operations.OpCommand.Start
import cats.{Order, Semigroup}
import zio.{Exit, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
final class Aggregate2ChannelsActor[Input0, Id0, Input1, Id1, Output, OutputId] private (
    val uniqueTag: String,
    val makeChannel0: MakeSignalChannel,
    val makeChannel1: MakeSignalChannel,
    val makeChannelState: MakeChannel[Output, OutputId],
    val merge: Aggregate2Channels[Input0, Input1, Output],
    val channelMayBeEmpty: Boolean,
    val enableTrace: Boolean
)(
    implicit val zioRuntime: zio.Runtime[zio.ZEnv],
    val statusSG: Semigroup[ChannelStatus],
    val statusO: Order[ChannelStatus]
) extends Actor with Stash with Timers with ActorLogging with State[Input0, Id0, Input1, Id1, Output, OutputId]
    with BasicBehavior[Input0, Id0, Input1, Id1, Output, OutputId] {
  override def preStart(): Unit = {
    super.preStart()
    val chStateRef = makeChannelState.make(context, self, channelIdState, channelTagState)
    val chState = ChannelCreated(
      channelId = channelIdState,
      tag = channelTagState,
      producer = chStateRef.x,
      consumer = self
    )
    if (enableTrace) {
      log.info(s"$uniqueTag. tag: ${chState.tag}. id: ${chState.channelId}")
    }
    context.become(behaviors(chState))
    self ! Start
  }

  private val tag: String = s"$uniqueTag. pre-start."

  private def behaviors(state: ChannelCreated): Receive = {
    case Start =>
      val effect = for {
        ch0Ref <- ZIO.effect(makeChannel0.make[Id0](context, self, channelTag0, channelId0))
        _ <- ZIO.when(enableTrace)(
              ZIO.effect(log.info(s"$tag. tag: $channelTag0. id: $channelId0"))
            )
        ch1Ref <- ZIO.effect(makeChannel1.make[Id1](context, self, channelTag1, channelId1))
        _ <- ZIO.when(enableTrace)(
              ZIO.effect(log.info(s"$tag. tag: $channelTag1. id: $channelId1"))
            )
      } yield (
        SignalChannelCreated(
          channelId = channelId0,
          tag = channelTag0,
          producer = ch0Ref.x,
          consumer = self
        ),
        SignalChannelCreated(
          channelId = channelId1,
          tag = channelTag1,
          producer = ch1Ref.x,
          consumer = self
        )
      )
      zioRuntime.unsafeRunAsync(effect) {
        case Exit.Failure(cause) =>
          shutdown()
        case Exit.Success((ch0, ch1)) =>
          unstashAll()
          basicBehavior(
            ch0 = ch0,
            ch0Status = ChannelStatus.Undefined,
            ch1 = ch1,
            ch1Status = ChannelStatus.Undefined,
            state = state,
            arrowRun = false,
            needDrain = false
          )
      }

    case msg: ChannelCreate =>
      if (enableTrace) {
        log.info(
          s"$tag. channel-create. forward."
        )
      }
      state.producer.forward(msg)

    case msg: ChannelDelete =>
      if (enableTrace) {
        log.info(
          s"$tag. channel-delete. forward."
        )
      }
      state.producer.forward(msg)

    case msg: SignalChannelCreate =>
      if (enableTrace) {
        log.info(
          s"$tag. signal-channel-create. forward."
        )
      }
      state.producer.forward(msg)

    case msg: SignalChannelDelete =>
      if (enableTrace) {
        log.info(
          s"$tag. signal-channel-delete. forward."
        )
      }
      state.producer.forward(msg)
  }

  def receive: Receive = Actor.emptyBehavior
}

object Aggregate2ChannelsActor {
  final def make[Input0, Id0, Input1, Id1, Output, OutputId](
      uniqueTag: String,
      makeChannel0: MakeSignalChannel,
      makeChannel1: MakeSignalChannel,
      makeChannelState: MakeChannel[Output, OutputId],
      channelMayBeEmpty: Boolean,
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      mergeFn: Aggregate2Fn[Input0, Input1, Output],
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
            new Aggregate2ChannelsActor(
              uniqueTag = uniqueTag,
              makeChannel0 = makeChannel0,
              makeChannel1 = makeChannel1,
              makeChannelState = makeChannelState,
              merge = Aggregate2Channels.make[Input0, Input1, Output, OutputId],
              channelMayBeEmpty = channelMayBeEmpty,
              enableTrace = enableTrace
            )
          )
        )
    )

  final def makeWithMerge[Input0, Id0, Input1, Id1, Output, OutputId](
      uniqueTag: String,
      makeChannel0: MakeSignalChannel,
      makeChannel1: MakeSignalChannel,
      makeChannelState: MakeChannel[Output, OutputId],
      mergeFn: Aggregate2Fn[Input0, Input1, Output],
      channelMayBeEmpty: Boolean,
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      statusSG: Semigroup[ChannelStatus],
      statusO: Order[ChannelStatus],
      o0: Order[Output],
      o1: Order[OutputId],
      r0: Output => OutputId
  ): ActorProducer[Output, OutputId] = {
    implicit val mfn: Aggregate2Fn[Input0, Input1, Output] = mergeFn
    make[Input0, Id0, Input1, Id1, Output, OutputId](
      uniqueTag,
      makeChannel0,
      makeChannel1,
      makeChannelState,
      channelMayBeEmpty,
      enableTrace
    )
  }
}
