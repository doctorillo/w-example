package bookingtour.core.actors.primitives.channel.accumulate

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.{SignalChannelCreate, SignalChannelDelete}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent._
import cats.instances.int._
import cats.instances.uuid._
import cats.syntax.eq._

/**
  * © Alexey Toroshchin 2019.
  */
protected[accumulate] trait BasicBehavior {
  _: Actor with ActorLogging with State =>

  protected final def basicBehavior(
      source: List[SignalChannelCreated],
      consumers: List[SignalChannelCreated]
  ): Unit =
    context.become(
      behaviors(
        source = source,
        consumers = consumers
      )
    )

  private final def behaviors(
      source: List[SignalChannelCreated],
      consumers: List[SignalChannelCreated]
  ): Receive = {
    case msg @ SignalChannelCreated(_, _, _, _) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. signal-channel-created received."
        )
      }
      basicBehavior(source :+ msg, consumers)

    case SignalChannelCreate(id, tag, consumer) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. signal-channel-create received."
        )
      }
      val s = SignalChannelCreated(channelId = id, tag = tag, producer = self, consumer = consumer)
      basicBehavior(source, consumers :+ s)
      consumer ! s

    case SignalChannelDelete(id, consumer) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. signal-channel-delete received."
        )
      }
      basicBehavior(source, consumers.filterNot(_.channelId === id))
      consumer ! SignalChannelDeleted(id)

    case SignalChannelDeleted(id) if source.exists(_.channelId === id) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. signal-channel-deleted received."
        )
      }
      if (source.length === 1) {
        shutdown()
      } else {
        basicBehavior(source.filterNot(_.channelId === id), consumers)
      }

    case SignalChannelStatusChanged(id, s) if source.exists(_.channelId === id) =>
      if (enableTrace) {
        log.info(s"$uniqueTag. signal-channel-status-changed received.")
      }
      consumers.foreach(x => x.consumer ! SignalChannelStatusChanged(x.channelId, s))

    case msg =>
      log.error(s"$uniqueTag. basic-behavior. unhandled $msg")
      shutdown()
  }
}
