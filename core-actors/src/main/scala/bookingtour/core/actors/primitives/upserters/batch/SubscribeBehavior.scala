package bookingtour.core.actors.primitives.upserters.batch

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.ChannelCreate
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated

/**
  * © Alexey Toroshchin 2019.
  */
protected[batch] trait SubscribeBehavior[PARAM, PARAM_ID] {
  _: Actor with ActorLogging with Stash with State[PARAM, PARAM_ID] with BasicBehavior[PARAM, PARAM_ID] =>

  private final def behaviors(): Receive = {
    case msg @ ChannelCreated(id, _, _, _) =>
      if (enableTrace) {
        log.info(s"$uniqueTag. ch: params. $id. created.")
      }
      unstashAll()
      basicBehavior(
        channel = msg,
        signals = List.empty,
        upserted = List.empty,
        pending = List.empty,
        error = List.empty,
        publishedStatus = ChannelStatus.Undefined,
        running = false
      )

    case _ =>
      stash()
  }

  protected final def subscribeBehavior(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. ch: params. create $channelId.")
    }
    producer0.x ! ChannelCreate(channelId, uniqueTag, self)
    context.become(behaviors())
  }
}
