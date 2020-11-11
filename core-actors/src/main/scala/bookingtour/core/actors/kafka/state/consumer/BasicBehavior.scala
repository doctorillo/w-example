package bookingtour.core.actors.kafka.state.consumer

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelEvent._
import bookingtour.protocols.core.actors.channels.query.ChannelFetchCommand.{
  Fetch,
  FetchWithKeyFilter,
  FetchWithKeyValueFilter,
  FetchWithValueFilter
}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelConsumerMessageReceived,
  EdgeTopicUnreachableReceived
}
import bookingtour.protocols.core.actors.operations.OpCommand.ReConnect
import cats.instances.string._
import cats.instances.uuid._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[consumer] trait BasicBehavior[Value, Id] {
  _: Actor with ActorLogging with State[Value, Id] =>

  private final val tag: String = s"$uniqueTag. basic-behavior"

  private final def reConnect(state: DStateConsumer.State[Value, Id]): Unit = {
    log.error(s"$uniqueTag. re-connect. attempt.")
    timerStart()
    basicBehavior(state.publishCreate())
  }

  protected final def basicBehavior(
      state: DStateConsumer.State[Value, Id]
  ): Unit =
    context.become(behaviors(state))

  private final def behaviors(
      state: DStateConsumer.State[Value, Id]
  ): Receive = {
    case msg: ChannelCommand =>
      state.stateChannel.x.forward(msg)

    case msg: ChannelSignalCommand =>
      state.stateChannel.x.forward(msg)

    case msg: Fetch =>
      state.stateChannel.x.forward(msg)

    case msg: FetchWithKeyFilter[_] =>
      state.stateChannel.x.forward(msg)

    case msg: FetchWithValueFilter[_] =>
      state.stateChannel.x.forward(msg)

    case msg: FetchWithKeyValueFilter[_, _] =>
      state.stateChannel.x.forward(msg)

    case EdgeTopicUnreachableReceived(_, topic, _) =>
      if (outputTopic === topic && !timerActive()) {
        log.error(s"$uniqueTag. edge-topic-unreachable-received. topic: $topic.")
        timerStart()
      }

    case ReConnect =>
      reConnect(state)

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if id === state.channelCreatedId =>
      if (enableTrace) {
        log.info(s"$tag. d-channel-created received.")
      }
      try {
        val data = msg.asInstanceOf[DChannelCreated]
        timerCancel()
        basicBehavior(state.receivedCreated(data))
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. d-channel-created received. ${thr.getMessage}.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, _, _) if id === state.channelDeletedId =>
      if (enableTrace) {
        log.info(s"$tag. d-channel-deleted received.")
      }
      timerStart()

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if id === state.channelEmptySnapshotId =>
      try {
        val data = msg.asInstanceOf[DChannelEmptySnapshotReceived]
        if (enableTrace) {
          log.info(s"$tag. d-channel-empty-snapshot-received.")
        }
        basicBehavior(state.receivedEmptySnapshot(data))
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. d-channel-empty-snapshot-received. ${thr.getMessage}.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if state.channelSnapshotId === id =>
      if (enableTrace) {
        log.info(s"$tag. d-channel-snapshot-received received.")
      }
      try {
        val data = msg.asInstanceOf[DChannelSnapshotReceived[Value]]
        basicBehavior(state.receivedSnapshot(data))
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. d-channel-snapshot-received. ${thr.getMessage}.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if state.channelItemCreatedId === id =>
      if (enableTrace) {
        log.info(s"$tag. d-channel-item-created received.")
      }
      try {
        val data = msg.asInstanceOf[DChannelItemCreated[Value]]
        basicBehavior(state.receivedItemCreated(data))
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. d-channel-item-created. ${thr.getMessage}.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if state.channelItemUpdatedId === id =>
      if (enableTrace) {
        log.info(s"$tag. d-channel-item-updated received.")
      }
      try {
        val data = msg.asInstanceOf[DChannelItemUpdated[Value]]
        basicBehavior(state.receivedItemUpdated(data))
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. d-channel-item-updated. ${thr.getMessage}.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if state.channelItemDeletedId === id =>
      if (enableTrace) {
        log.info(s"$tag. d-channel-item-deleted received.")
      }
      try {
        val data = msg.asInstanceOf[DChannelItemDeleted[Value]]
        basicBehavior(state.receivedItemDeleted(data))
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. d-channel-item-deleted. ${thr.getMessage}.")
      }

    case EdgeChannelConsumerMessageReceived(id, _, msg, _) if state.channelStatusChangedId === id =>
      if (enableTrace) {
        log.info(s"$tag. d-channel-status-changed received.")
      }
      try {
        val data = msg.asInstanceOf[DChannelStatusChanged]
        basicBehavior(state.receivedStatusChanged(data))
      } catch {
        case thr: Throwable =>
          log.error(s"$tag. d-channel-status-changed. ${thr.getMessage}.")
      }

    case msg: EdgeChannelConsumerMessageReceived[_] =>
      log.error(
        s"$tag. receive expired consumer message after quick restart. channel: ${msg.envelope.channel}. expired-at: ${msg.envelope.expiredAt}"
      )

    case msg =>
      log.error(s"$tag. unhandled message: $msg.")
      shutdown()
  }
}
