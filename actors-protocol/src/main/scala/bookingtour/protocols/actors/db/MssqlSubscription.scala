package bookingtour.protocols.actors.db

import java.util.UUID

import akka.actor.ActorRef

/**
  * Created by d0ct0r on 2019-10-24.
  */
sealed abstract class MssqlSubscription

object MssqlSubscription extends MssqlSubscription {
  sealed abstract class Command

  sealed abstract class Event

  final case class Subscribe(subscriber: ActorRef) extends Command

  final case class Unsubscribe(id: UUID, subscriber: ActorRef) extends Command

  final case class SubscriptionReceived(
      id: UUID,
      producer: ActorRef,
      subscriber: ActorRef
  ) extends Event
}
