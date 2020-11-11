package bookingtour.core.actors.primitives.channel.accumulate

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.SignalChannelCreate
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class AccumulatorChangeActor private (
    val uniqueTag: String,
    val enableTrace: Boolean
) extends Actor with ActorLogging with State with BasicBehavior {

  override def preStart(): Unit = {
    super.preStart()
    basicBehavior(List.empty, List.empty)
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.error(s"$uniqueTag. post-restart. {}.", reason)
    shutdown()
  }

  def receive: Receive = Actor.emptyBehavior
}

object AccumulatorChangeActor {
  @newtype final case class AccumulatorChangeActorRef(x: ActorRef)

  final def make(uniqueTag: String, trace: Boolean)(
      implicit ctx: ActorSystem
  ): AccumulatorChangeActorRef =
    ctx
      .actorOf(Props(new AccumulatorChangeActor(uniqueTag = uniqueTag, enableTrace = trace)))
      .coerce[AccumulatorChangeActorRef]

  final def subscribe(
      accumulator: AccumulatorChangeActorRef,
      source: ActorProducer[_, _],
      tag: String
  ): Unit =
    source.x.tell(
      msg = SignalChannelCreate(channelId = UUID.randomUUID(), tag = tag, accumulator.x),
      sender = accumulator.x
    )
}
