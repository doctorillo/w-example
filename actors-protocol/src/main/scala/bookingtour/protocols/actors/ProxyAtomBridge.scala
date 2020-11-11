package bookingtour.protocols.actors

import java.time.LocalDateTime

import akka.actor.ActorRef

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ProxyAtomBridge
object ProxyAtomBridge {
  sealed abstract class Command
  final case class Get[A](ident: A, after: LocalDateTime, atom: ActorRef)     extends Command
  final case class Deleted[A](ident: A, after: LocalDateTime, atom: ActorRef) extends Command
  final case class WeakUpdate[A](value: A)                                    extends Command
  final case class WeakDelete[A](valueId: A)                                  extends Command
}
