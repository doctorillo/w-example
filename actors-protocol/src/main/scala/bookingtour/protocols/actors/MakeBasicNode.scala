package bookingtour.protocols.actors

import akka.actor.{ActorContext, ActorRef}

/**
  * © Alexey Toroshchin 2019.
  */
trait MakeBasicNode {
  def make(ctx: ActorContext, uniqueTag: String): ActorRef
}
