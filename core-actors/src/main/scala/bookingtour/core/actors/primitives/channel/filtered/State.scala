package bookingtour.core.actors.primitives.channel.filtered

import akka.actor.{Actor, ActorLogging, ActorRef}
import cats.Order
import cats.data.Reader

/**
  * © Alexey Toroshchin 2019.
  */
protected[filtered] trait State[K, IN, ID] {
  _: Actor with ActorLogging =>

  protected val uniqueTag: String
  protected val key: K
  protected val producer0: ActorRef
  protected val selector: K => IN => Boolean
  protected val enableTrace: Boolean

  implicit protected val chR: Reader[IN, ID]
  implicit protected val chO: Order[IN]
  implicit protected val chIdO: Order[ID]

  protected def select(x: IN): Boolean = selector(key)(x)

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown")
    }
    context.stop(self)
  }
}
