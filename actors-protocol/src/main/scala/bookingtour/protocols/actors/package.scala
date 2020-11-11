package bookingtour.protocols

import akka.actor.ActorRef
import io.estatico.newtype.macros.newtype

/**
  * © Alexey Toroshchin 2019.
  */
package object actors {
  @newtype final case class ActorUnit(x: ActorRef)
  @newtype final case class ActorProducer[Value, Id](x: ActorRef)
  @newtype final case class ActorConsumer[Value, Id](x: ActorRef)
}
