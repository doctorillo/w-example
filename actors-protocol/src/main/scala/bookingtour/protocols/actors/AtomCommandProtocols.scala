package bookingtour.protocols.actors

import akka.actor.ActorRef
import cats.data.NonEmptyChain

object AtomCommandProtocols {
  sealed abstract class Protocols extends Product with Serializable
  sealed abstract class Command   extends Protocols
  sealed abstract class Event     extends Protocols

  final case object FetchState                extends Command
  final case object Shutdown                  extends Command
  final case class GetAll(replayTo: ActorRef) extends Command

  final case class GetFiltered[A](filter: A => Boolean, replayTo: ActorRef) extends Command

  final case class GetLastUpdate(replayTo: ActorRef) extends Command

  final case class WeakUpdate[A](value: A) extends Command

  final case class WeakDelete[A](valueId: A) extends Command

  final case class EmptyAtomAnswerReceived(atomId: String)                     extends Event
  final case class AtomAnswerReceived[A](atomId: String, xs: NonEmptyChain[A]) extends Event
  final case class LastUpdateReceived[A](atomId: String, update: A)            extends Event
}
