package bookingtour.core.actors.primitives.channel.signal

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.SignalChannelCreate
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated

/**
  * © Alexey Toroshchin 2019.
  */
protected[signal] trait SubscribeBehavior[Id] {
  _: Actor with ActorLogging with Stash with State[Id] with BasicBehavior[Id] =>

  private final def behaviors(): Receive = {
    case msg: SignalChannelCreated =>
      if (enableTrace) {
        log.info(s"$uniqueTag. channel-signal-created received.")
      }
      unstashAll()
      basicBehavior(msg, ChannelStatus.Undefined)

    case msg =>
      if (enableTrace) {
        log.info(s"$uniqueTag. stash. $msg")
      }
      stash()
  }

  protected final def subscribeBehavior(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. channel-signal-create.")
    }
    context.become(behaviors())
    valueProducer.x ! SignalChannelCreate(UUID.randomUUID(), uniqueTag, self)
  }
}
