package bookingtour.core.actors.primitives.channel.basic

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.actors.aggregators._
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand._
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent._
import bookingtour.protocols.core.actors.internal.PendingItem
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.instances.uuid._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[basic] trait ConsumerBehavior[Value, Id, PartitionKey] {
  _: Actor with ActorLogging with State[Value, Id, PartitionKey] with BasicBehavior[Value, Id, PartitionKey] =>

  protected final def consumerBehaviors(
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
    case ChannelCreate(channelId, tag, replayTo) =>
      val s = ChannelCreated(channelId = channelId, tag = tag, producer = self, consumer = replayTo)
      val d = state.toList.flatMap(_._2)
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-create received. tag: $tag. state: ${d.length}. status: $publishedStatus. consumers: ${consumers.length + 1}. signals: ${signals.length}."
        )
      }
      replayTo ! s
      if (running) {
        startProcessing(
          sequenceId = sequenceId,
          consumers = consumers,
          pendingConsumers = pendingConsumers :+ s,
          signals = signals,
          state = state,
          publishedStatus = publishedStatus,
          pendingStatus = pendingStatus,
          pending = pending,
          running = running
        )
      } else {
        sendToChannel(uniqueTag, log, enableTrace, s, d)
        replayTo ! ChannelStatusChanged(s.channelId, publishedStatus)
        startProcessing(
          sequenceId = sequenceId,
          consumers = consumers :+ s,
          pendingConsumers = pendingConsumers,
          signals = signals,
          state = state,
          publishedStatus = publishedStatus,
          pendingStatus = pendingStatus,
          pending = pending,
          running = running
        )
      }

    case ChannelDelete(id, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-delete received."
        )
      }
      replayTo ! ChannelDeleted(id)
      startProcessing(
        sequenceId = sequenceId,
        consumers = consumers.filterNot(_.channelId === id),
        pendingConsumers = pendingConsumers.filterNot(_.channelId === id),
        signals = signals,
        state = state,
        publishedStatus = publishedStatus,
        pendingStatus = pendingStatus,
        pending = pending,
        running = running
      )
  }
}
