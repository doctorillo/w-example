package bookingtour.core.actors.kafka.state.producer

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent._
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelCommand.{
  DChannelCreate,
  DChannelDelete
}
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelEvent._
import bookingtour.protocols.core.actors.kafka.EdgeEvent._
import bookingtour.protocols.core.actors.kafka.EdgeProducerCommand.EdgePublish
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.instances.uuid._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[producer] trait BasicBehavior[Value, Id] {
  _: Actor with ActorLogging with State[Value, Id] =>

  private final def consumerBehaviors(
      state: DStateProducer.State
  ): Receive = {
    case msg: DStateProducer.SubscriptionChannel =>
      if (enableTrace) {
        log.info(s"$uniqueTag. channel received.")
      }
      basicBehavior(state.publishCreated[Value](msg))

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if id === state.channelCreateId =>
      val tag = s"$uniqueTag. d-channel-create"
      try {
        val data = msg.asInstanceOf[DChannelCreate]
        state.subscriptions.find(_.subscription.sessionId === data.sessionId) match {
          case None =>
            if (enableTrace) {
              log.info(s"$tag. consumer-topic: ${data.consumerTopic}. received new session.")
            }
            val c = DChannelCreated(
              sessionId = data.sessionId,
              announceSequenceId = SequenceNr.Zero.next,
              sequenceId = SequenceNr.Zero,
              targetTag = data.targetTag,
              consumerTopic = data.consumerTopic
            )
            makeSubscription(c, self)

          case Some(x) =>
            log.error(s"$tag received. tag: ${data.targetTag}. session exist.")
            x.createdChannel.replayTo ! EdgePublish(
              id = x.createdChannel.id,
              channel = makeChannel(x.subscription),
              msg = x.subscription,
              expiredAt = expiredAt()
            )
        }
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. ${thr.getMessage}.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if id === state.deleteChannel.id =>
      val tag = s"$uniqueTag. d-channel-delete receive"
      try {
        val data = msg.asInstanceOf[DChannelDelete]
        state.subscriptions
          .find(_.subscription.sessionId === data.sessionId) match {
          case None =>
            log.error(s"$tag. session not found.")

          case Some(s) =>
            if (enableTrace) {
              log.info(s"$tag. session removed.")
            }
            val deletedId = s.announceSequenceId
            basicBehavior(state.deleteSubscription(s.subscription.sessionId))
            s.deletedChannel.replayTo ! EdgePublish(
              id = s.deletedChannel.id,
              channel = makeChannel(s.subscription),
              msg = DChannelDeleted(
                sessionId = s.subscription.sessionId,
                announceSequenceId = SequenceNr.Zero,
                sequenceId = deletedId
              ),
              expiredAt = expiredAt()
            )
        }
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. ${thr.getMessage}.")
      }
  }

  private final def behaviors(
      state: DStateProducer.State
  ): Receive = {
    case ChannelStatusChanged(channelId, _status) if channelId === state.channelStateId =>
      val tag = s"$uniqueTag. channel-status-changed received"
      if (enableTrace) {
        log.info(s"$tag. status: ${_status}.")
      }
      basicBehavior(state.publishStatus(_status))

    case ChannelEmptySnapshotReceived(channelId) if channelId === state.channelStateId =>
      val tag = s"$uniqueTag. channel-empty-snapshot-received"
      if (enableTrace) {
        log.info(s"$tag.")
      }
      basicBehavior(state.publishEmptyState())

    case ChannelSnapshotReceived(channelId, data) if channelId === state.channelStateId =>
      val tag = s"$uniqueTag. channel-snapshot-received"
      if (enableTrace) {
        log.info(s"$tag. ${data.length} items.")
      }
      basicBehavior(state.publishState(data))

    case ChannelItemCreated(channelId, data) if channelId === state.channelStateId =>
      val tag = s"$uniqueTag. channel-item-created"
      if (enableTrace) {
        log.info(s"$tag. ${data.length} items.")
      }
      basicBehavior(state.publishItemCreated(data))

    case ChannelItemUpdated(channelId, data) if channelId === state.channelStateId =>
      val tag = s"$uniqueTag. channel-item-updated"
      if (enableTrace) {
        log.info(s"$tag. ${data.length} items.")
      }
      basicBehavior(state.publishItemUpdated(data))

    case ChannelItemDeleted(channelId, data) if channelId === state.channelStateId =>
      val tag = s"$uniqueTag. channel-item-deleted"
      if (enableTrace) {
        log.info(s"$tag. ${data.length} items.")
      }
      basicBehavior(state.publishItemDeleted(data))

    case EdgeTopicUnreachableReceived(_, topic, _) =>
      log.error(s"$uniqueTag. edge-topic-unreachable-received. topic: $topic.")
    //TODO |ZZZ| basicBehavior(state.deleteUnreachableTopic(topic))

    case _: EdgeProducerChannelCreated =>
    case _: EdgeProducerChannelDeleted =>
    case _: EdgeConsumerChannelDeleted =>
    case msg =>
      log.error(s"$uniqueTag. receive unhandled msg ${msg.getClass}.")
      log.error(s"$uniqueTag. shutdown.")
      shutdown()
  }

  protected def basicBehavior(
      state: DStateProducer.State
  ): Unit = context.become(consumerBehaviors(state).orElse(behaviors(state)))
}
