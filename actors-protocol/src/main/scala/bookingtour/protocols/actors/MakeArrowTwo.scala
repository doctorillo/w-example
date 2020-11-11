package bookingtour.protocols.actors

import akka.actor.{ActorContext, ActorRef}

/**
  * © Alexey Toroshchin 2019.
  */
trait MakeArrowTwo[I0, ID0, I1, ID1, O, OID] {
  def make(
      ctx: ActorContext,
      uniqueTag: String,
      manager: ActorRef,
      channel0: ActorRef,
      channel1: ActorRef,
      channelState: ActorRef
  ): ActorRef
}
