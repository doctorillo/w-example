package bookingtour.core.actors.primitives.channel.signal

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef}
import bookingtour.protocols.actors.ActorProducer
import cats.Order

/**
  * © Alexey Toroshchin 2019.
  */
protected[signal] trait State[Id] {
  _: Actor with ActorLogging =>

  protected val uniqueTag: String
  protected val channelId: UUID
  protected val manager: ActorRef
  protected val valueProducer: ActorProducer[_, Id]
  protected val enableTrace: Boolean

  implicit protected val idO: Order[Id]

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown")
    }
    context.stop(self)
  }
}
