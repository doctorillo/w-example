package bookingtour.core.actors.primitives.channel.basic

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.{SignalChannelCreate, SignalChannelDelete}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent._
import bookingtour.protocols.core.actors.internal.PendingItem
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.instances.uuid._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[basic] trait SignalBehavior[Value, Id, PartitionKey] {
  _: Actor with ActorLogging with State[Value, Id, PartitionKey] with BasicBehavior[Value, Id, PartitionKey] =>

  protected final def signalBehaviors(
      sequenceId: SequenceNr,
      consumers: List[ChannelCreated],
      pendingConsumers: List[ChannelCreated],
      signals: List[SignalChannelCreated],
      state: Map[PartitionKey, List[Value]],
      publishedStatus: ChannelStatus,
      pendingStatus: Option[(SequenceNr, ChannelStatus)],
      pending: List[PendingItem],
      running: Boolean
  ): Receive = {
    case SignalChannelCreate(id, tag, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. signal-channel-create received. tag: $tag. status: $publishedStatus. consumers: ${consumers.length}. signals: ${signals.length + 1}."
        )
      }
      val s = SignalChannelCreated(channelId = id, tag = tag, producer = self, consumer = replayTo)
      replayTo ! s
      replayTo ! SignalChannelStatusChanged(id, publishedStatus)
      val xs = signals :+ s
      basicBehavior(
        sequenceId = sequenceId,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = xs,
        state = state,
        publishedStatus = publishedStatus,
        pendingStatus = pendingStatus,
        pending = pending,
        running = running
      )

    case SignalChannelDelete(id, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. signal-channel-delete received."
        )
      }
      replayTo ! SignalChannelDeleted(id)
      basicBehavior(
        sequenceId = sequenceId,
        consumers = consumers,
        pendingConsumers = pendingConsumers,
        signals = signals.filterNot(_.channelId === id),
        state = state,
        publishedStatus = publishedStatus,
        pendingStatus = pendingStatus,
        pending = pending,
        running = running
      )
  }
}
