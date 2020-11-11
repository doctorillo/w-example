package bookingtour.protocols.actors.proxy

import akka.actor.ActorRef

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class LifeCycle
object LifeCycle {
  sealed abstract class Command

  sealed abstract class Event

  final case class NotifyIfReadyForSubscribe(replayTo: ActorRef)

  final case class ProxyReadyForSubscribe(replayTo: ActorRef)
}
