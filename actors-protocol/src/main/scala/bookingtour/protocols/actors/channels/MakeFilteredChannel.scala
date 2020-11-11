package bookingtour.protocols.actors.channels

import akka.actor.{ActorContext, ActorRef}
import bookingtour.protocols.actors.ActorProducer

/**
  * © Alexey Toroshchin 2019.
  */
trait MakeFilteredChannel[Key, Value, Id] {
  val valueProducer: ActorRef
  val selector: Key => Value => Boolean
  val enableTrace: Boolean

  def make(
      ctx: ActorContext,
      managerRef: ActorRef,
      uniqueTag: String,
      key: Key
  ): ActorProducer[Value, Id]
}
