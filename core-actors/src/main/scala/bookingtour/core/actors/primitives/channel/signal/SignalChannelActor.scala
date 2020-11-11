package bookingtour.core.actors.primitives.channel.signal

import java.util.UUID

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props, Stash}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.channels.MakeSignalChannel
import bookingtour.protocols.actors.channels.MakeSignalChannel.SignalChannelActorRef
import cats.Order
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class SignalChannelActor[Id] private (
    val uniqueTag: String,
    val channelId: UUID,
    val manager: ActorRef,
    val valueProducer: ActorProducer[_, Id],
    val enableTrace: Boolean
)(implicit val idO: Order[Id])
    extends Actor with Stash with ActorLogging with State[Id] with SubscribeBehavior[Id] with BasicBehavior[Id] {
  override def preStart(): Unit = {
    super.preStart()
    subscribeBehavior()
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.error(s"$uniqueTag. post-restart. {}.", reason)
    shutdown()
  }

  def receive: Receive = Actor.emptyBehavior
}

object SignalChannelActor {
  final def makeChannel[Id](
      producer: ActorProducer[_, Id],
      trace: Boolean
  )(implicit idO: Order[Id]): MakeSignalChannel =
    new MakeSignalChannel {
      val enableTrace: Boolean = trace

      def make[Id](
          ctx: ActorContext,
          managerRef: ActorRef,
          uniqueTag: String,
          channelId: UUID
      ): SignalChannelActorRef[Id] =
        ctx
          .actorOf(
            Props(
              new SignalChannelActor(
                uniqueTag = uniqueTag,
                channelId = channelId,
                manager = managerRef,
                valueProducer = producer,
                enableTrace = enableTrace
              )
            )
          )
          .coerce[SignalChannelActorRef[Id]]
    }
}
