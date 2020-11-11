package bookingtour.protocols.actors.proxy

import java.util.UUID

import akka.actor.ActorRef

/**
  * © Alexey Toroshchin 2019.
  */
object ProxySubscription {
  sealed abstract class Protocol extends Product with Serializable

  sealed abstract class Command extends Protocol

  sealed abstract class Event extends Protocol

  final case class Subscribe(sessionId: UUID, subscriber: ActorRef) extends Command

  final case class SubscribeByIdent[A](sessionId: UUID, ident: A, subscriber: ActorRef) extends Command

  final case class Unsubscribe(sessionId: UUID) extends Command

  final case class SubscriptionChannelCreated(sessionId: UUID, subscriber: ActorRef) extends Event

  final case class SubscriptionCompleted(sessionId: UUID, publisher: ActorRef) extends Event

  final case class SubscriptionCanceled(sessionId: UUID, publisher: ActorRef) extends Event

  final case object ReadyForSubscribe extends Event
}
