package bookingtour.core.actors.kafka.state.producer

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.ChannelCreate
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated

/**
  * © Alexey Toroshchin 2019.
  */
protected[producer] trait CreateDataChannelBehavior[Value, Id] {
  _: Actor with ActorLogging with Stash with State[Value, Id] with ChannelCreateBehavior[Value, Id] =>

  private final val tag: String = s"$uniqueTag. tag: $targetTag. create-data-channel behavior."

  private final def behaviors(): Receive = {
    case msg: ChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag subscribed to data-link. ${msg.channelId}")
      }
      channelCreateBehavior(msg)

    case _ =>
      stash()
  }

  protected def createDataChannelBehavior(): Unit = {
    context.become(behaviors())
    val id = UUID.randomUUID()
    dataLink.x ! ChannelCreate(id, s"$uniqueTag:data-link", self)
  }
}
