package bookingtour.protocols.core.messages

import java.time.Instant

import akka.actor.ActorRef

/**
  * © Alexey Toroshchin 2019.
  */
final case class RunnableQuery[A](query: A, expiredAt: Instant, replayTo: ActorRef)
