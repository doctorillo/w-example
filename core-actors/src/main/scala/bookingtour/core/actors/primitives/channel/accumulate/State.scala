package bookingtour.core.actors.primitives.channel.accumulate

import akka.actor.{Actor, ActorLogging}

/**
  * © Alexey Toroshchin 2019.
  */
protected[accumulate] trait State {
  _: Actor with ActorLogging =>

  protected val uniqueTag: String
  protected val enableTrace: Boolean

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown")
    }
    context.stop(self)
  }
}
