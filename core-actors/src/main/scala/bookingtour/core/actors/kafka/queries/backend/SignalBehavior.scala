package bookingtour.core.actors.kafka.queries.backend

import java.util.UUID

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.SignalChannelCreate
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent.SignalChannelCreated

/**
  * © Alexey Toroshchin 2019.
  */
protected[backend] trait SignalBehavior[R, Query, Answer] {
  _: Actor with ActorLogging with Stash with State[R, Query, Answer] with CreateBehavior[R, Query, Answer] =>

  private final val tag: String =
    s"$uniqueTag. topic: $targetTag. signal-behavior."

  private final def behaviors(): Receive = {
    case msg: SignalChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag signal-channel-created.")
      }
      createBehavior(signalChannel = msg)

    case _ =>
      stash()
  }

  protected def signalBehavior(): Unit = {
    context.become(behaviors())
    stateRef.x ! SignalChannelCreate(
      channelId = UUID.randomUUID(),
      tag = uniqueTag,
      consumer = self
    )
  }
}
