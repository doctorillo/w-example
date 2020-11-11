package bookingtour.core.actors.primitives.channel.filtered

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props, Stash}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.channels.MakeFilteredChannel
import cats.Order
import cats.data.Reader
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class ChannelFilteredActor[K, IN, ID] private (
    val uniqueTag: String,
    val key: K,
    val producer0: ActorRef,
    val selector: K => IN => Boolean,
    val enableTrace: Boolean
)(
    implicit val chR: Reader[IN, ID],
    val chO: Order[IN],
    val chIdO: Order[ID]
) extends Actor with Stash with ActorLogging with State[K, IN, ID] with SubscribeBehavior[K, IN, ID]
    with BasicBehavior[K, IN, ID] {
  override def preStart(): Unit = {
    super.preStart()
    subscribeBehavior()
  }

  def receive: Receive = Actor.emptyBehavior
}

object ChannelFilteredActor {
  final type MakeChannelInstance[K, IN, ID] =
    (ActorContext, String, K, ActorRef) => ActorProducer[IN, ID]

  final def makeChannelFiltered[K, IN, ID](
      producer: ActorRef,
      select: K => IN => Boolean,
      trace: Boolean
  )(
      implicit chR: Reader[IN, ID],
      chO: Order[IN],
      chIdO: Order[ID]
  ): MakeFilteredChannel[K, IN, ID] = new MakeFilteredChannel[K, IN, ID] {
    val valueProducer: ActorRef      = producer
    val selector: K => IN => Boolean = select
    val enableTrace: Boolean         = trace

    def make(
        ctx: ActorContext,
        managerRef: ActorRef,
        uniqueTag: String,
        key: K
    ): ActorProducer[IN, ID] =
      ctx
        .actorOf(
          Props(
            new ChannelFilteredActor(
              uniqueTag = uniqueTag,
              producer0 = valueProducer,
              key = key,
              selector = selector,
              enableTrace = enableTrace
            )
          )
        )
        .coerce[ActorProducer[IN, ID]]
  }
}
