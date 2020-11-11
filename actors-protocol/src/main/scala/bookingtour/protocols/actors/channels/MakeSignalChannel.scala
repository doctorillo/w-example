package bookingtour.protocols.actors.channels

import java.util.UUID

import akka.actor.{ActorContext, ActorRef}
import bookingtour.protocols.actors.channels.MakeSignalChannel.SignalChannelActorRef
import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2019.
  */
trait MakeSignalChannel {
  // val valueProducer: ActorRef
  val enableTrace: Boolean

  def make[Id](
      ctx: ActorContext,
      managerRef: ActorRef,
      uniqueTag: String,
      channelId: UUID
  ): SignalChannelActorRef[Id]
}

object MakeSignalChannel {
  @newtype final case class SignalChannelActorRef[Id](x: ActorRef)
}
