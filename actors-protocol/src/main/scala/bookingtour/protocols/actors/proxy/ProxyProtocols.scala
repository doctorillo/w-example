package bookingtour.protocols.actors.proxy

import akka.actor.ActorRef

/**
  * © Alexey Toroshchin 2019.
  */
object ProxyProtocols {
  sealed abstract class Protocols extends Product with Serializable

  sealed abstract class Command extends Protocols

  sealed abstract class Event extends Protocols

  final case class Shutdown(replayTo: ActorRef) extends Command

  final case class Complete(proxyRef: ActorRef) extends Event
}
