package bookingtour.core.actors.primitives.transforms.transform

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Stash}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.aggregators.TransformChannel
import bookingtour.protocols.actors.channels.MakeChannel
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import cats.{Order, Semigroup}
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class TransformChannelActor[Input, Value, Id] private (
    val uniqueTag: String,
    val producer: ActorProducer[Input, _],
    val makeChannelState: MakeChannel[Value, Id],
    val transform: TransformChannel[Input, Value],
    val enableTrace: Boolean
)(
    implicit val valueR: Value => Id,
    val idO: Order[Id],
    val statusSG: Semigroup[ChannelStatus],
    val zioRuntime: zio.Runtime[zio.ZEnv],
    val statusO: Order[ChannelStatus]
) extends Actor with Stash with ActorLogging with State[Input, Value, Id]
    with CreateDataChannelBehavior[Input, Value, Id] with BasicBehavior[Input, Value, Id] {
  override def preStart(): Unit = {
    super.preStart()
    val stateChRef = makeChannelState.make(context, self, channelIdState, channelTagState)
    val stateCh = ChannelCreated(
      channelId = channelIdState,
      tag = channelTagState,
      producer = stateChRef.x,
      consumer = self
    )
    createDataChannelBehavior(stateCh = stateCh)
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.error(s"$uniqueTag. post-restart. {}", reason)
    shutdown()
  }

  def receive: Receive = Actor.emptyBehavior
}

object TransformChannelActor {
  final def makeMapper[Input, Value, Id](
      uniqueTag: String,
      producer: ActorProducer[Input, _],
      makeChannelState: MakeChannel[Value, Id],
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      fn: Input => Value,
      valueR: Value => Id,
      vO: Order[Value],
      idO: Order[Id],
      statusM: Semigroup[ChannelStatus],
      statusO: Order[ChannelStatus]
  ): ActorProducer[Value, Id] = {
    implicit val a: Ordering[Value] = vO.toOrdering
    ctx
      .actorOf(
        Props(
          new TransformChannelActor(
            uniqueTag = uniqueTag,
            producer = producer,
            makeChannelState = makeChannelState,
            transform = TransformChannel.makeMapper[Input, Value],
            enableTrace = enableTrace
          )
        )
      )
      .coerce[ActorProducer[Value, Id]]
  }

  final def makeMany[Input, Value, Id](
      uniqueTag: String,
      producer: ActorProducer[Input, _],
      makeChannelState: MakeChannel[Value, Id],
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      fn: Input => List[Value],
      valueR: Value => Id,
      vO: Order[Value],
      idO: Order[Id],
      statusM: Semigroup[ChannelStatus],
      statusO: Order[ChannelStatus]
  ): ActorProducer[Value, Id] = {
    implicit val a: Ordering[Value] = vO.toOrdering
    ctx
      .actorOf(
        Props(
          new TransformChannelActor(
            uniqueTag = uniqueTag,
            producer = producer,
            makeChannelState = makeChannelState,
            transform = TransformChannel.makeMany[Input, Value],
            enableTrace = enableTrace
          )
        )
      )
      .coerce[ActorProducer[Value, Id]]
  }

  final def makeInputMany[Input, Value, Id](
      uniqueTag: String,
      producer: ActorProducer[Input, _],
      makeChannelState: MakeChannel[Value, Id],
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      fn: List[Input] => List[Value],
      valueR: Value => Id,
      vO: Order[Value],
      idO: Order[Id],
      statusM: Semigroup[ChannelStatus],
      statusO: Order[ChannelStatus]
  ): ActorProducer[Value, Id] = {
    implicit val a: Ordering[Value] = vO.toOrdering
    ctx
      .actorOf(
        Props(
          new TransformChannelActor(
            uniqueTag = uniqueTag,
            producer = producer,
            makeChannelState = makeChannelState,
            transform = TransformChannel.makeInputMany[Input, Value],
            enableTrace = enableTrace
          )
        )
      )
      .coerce[ActorProducer[Value, Id]]
  }

  final def makeFilter[Input, Value, Id](
      uniqueTag: String,
      producer: ActorProducer[Input, _],
      makeChannelState: MakeChannel[Value, Id],
      enableTrace: Boolean
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      fn: Input => Option[Value],
      valueR: Value => Id,
      vO: Order[Value],
      idO: Order[Id],
      statusM: Semigroup[ChannelStatus],
      statusO: Order[ChannelStatus]
  ): ActorProducer[Value, Id] = {
    implicit val a: Ordering[Value] = vO.toOrdering
    ctx
      .actorOf(
        Props(
          new TransformChannelActor(
            uniqueTag = uniqueTag,
            producer = producer,
            makeChannelState = makeChannelState,
            transform = TransformChannel.makeFilter(fn),
            enableTrace = enableTrace
          )
        )
      )
      .coerce[ActorProducer[Value, Id]]
  }
}
