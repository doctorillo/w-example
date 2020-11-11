package bookingtour.protocols.actors

import akka.actor.{ActorContext, ActorRef}

/**
  * © Alexey Toroshchin 2019.
  */
trait MakeKeyedNode[T] {
  def make(ctx: ActorContext, uniqueTag: String, key: T): ActorRef
}
