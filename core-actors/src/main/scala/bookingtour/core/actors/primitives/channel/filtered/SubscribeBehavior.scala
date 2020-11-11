package bookingtour.core.actors.primitives.channel.filtered

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.ChannelCreate
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated

/**
  * © Alexey Toroshchin 2019.
  */
protected[filtered] trait SubscribeBehavior[K, IN, ID] {
  _: Actor with ActorLogging with Stash with State[K, IN, ID] with BasicBehavior[K, IN, ID] =>

  private final def behaviors(): Receive = {
    case msg: ChannelCreated =>
      if (enableTrace) {
        log.info(s"$uniqueTag. channel-created-received.")
      }
      unstashAll()
      basicBehavior(msg, List.empty, ChannelStatus.Undefined)

    case _ =>
      stash()
  }

  protected final def subscribeBehavior(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. channel-create.")
    }
    producer0 ! ChannelCreate(UUID.randomUUID(), uniqueTag, self)
    context.become(behaviors())
  }
}
