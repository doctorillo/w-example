package bookingtour.protocols.actors.channels

import java.util.UUID

import akka.actor.{ActorContext, ActorRef}
import bookingtour.protocols.actors.ActorProducer

/**
  * © Alexey Toroshchin 2019.
  */
trait MakeChannel[Value, Id] {
  val enableTrace: Boolean

  def make(
      ctx: ActorContext,
      managerRef: ActorRef,
      managerChannelId: UUID,
      uniqueTag: String
  ): ActorProducer[Value, Id]
}
