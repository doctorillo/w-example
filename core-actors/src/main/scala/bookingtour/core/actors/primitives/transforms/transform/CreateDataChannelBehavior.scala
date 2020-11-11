package bookingtour.core.actors.primitives.transforms.transform

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.{ChannelCreate, ChannelDelete}
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.{SignalChannelCreate, SignalChannelDelete}

/**
  * © Alexey Toroshchin 2019.
  */
protected[transform] trait CreateDataChannelBehavior[Input, Value, Id] {
  _: Actor with Stash with ActorLogging with State[Input, Value, Id] with BasicBehavior[Input, Value, Id] =>

  private final val tag: String = s"$uniqueTag. create-data-channel behavior"

  private final def behaviors(stateCh: ChannelCreated): Receive = {
    case msg: ChannelCreate =>
      if (enableTrace) {
        log.info(
          s"$tag. channel-create. forward."
        )
      }
      stateCh.producer.forward(msg)

    case msg: ChannelDelete =>
      if (enableTrace) {
        log.info(
          s"$tag. channel-delete. forward."
        )
      }
      stateCh.producer.forward(msg)

    case msg: SignalChannelCreate =>
      if (enableTrace) {
        log.info(
          s"$tag. signal-channel-create. forward."
        )
      }
      stateCh.producer.forward(msg)

    case msg: SignalChannelDelete =>
      if (enableTrace) {
        log.info(
          s"$tag. signal-channel-delete. forward."
        )
      }
      stateCh.producer.forward(msg)

    case msg: ChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag. channel-created. id: ${msg.channelId}.")
      }
      unstashAll()
      basicBehavior(
        input = msg,
        state = stateCh,
        pending = List.empty,
        running = false
      )

    case _ =>
      stash()
  }

  protected def createDataChannelBehavior(stateCh: ChannelCreated): Unit = {
    context.become(behaviors(stateCh: ChannelCreated))
    producer.x ! ChannelCreate(channelId = channelId0, channelTag0, self)
  }
}
