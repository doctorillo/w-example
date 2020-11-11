package bookingtour.core.actors.primitives.channel.basic

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.query.ChannelFetchCommand._
import bookingtour.protocols.core.actors.channels.query.ChannelFetchEvent.{ErrorReceived, StatusReceived}
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[basic] trait QueryBehavior[Value, Id, PartitionKey] {
  _: Actor with ActorLogging with State[Value, Id, PartitionKey] with BasicBehavior[Value, Id, PartitionKey] =>

  protected final def queryBehaviors(
      state: Map[PartitionKey, List[Value]],
      publishedStatus: ChannelStatus,
      running: Boolean
  ): Receive = {
    case FetchStatus(channelId, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-status received. state: ${state.size}. status: $publishedStatus."
        )
      }
      replayTo ! StatusReceived(channelId, publishedStatus)

    case FetchIfReady(channelId, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-if-ready received. state: ${state.size}. status: $publishedStatus."
        )
      }
      if (publishedStatus === ChannelStatus.Ready && !running) {
        replayMsg(channelId, replayTo, state)
      } else {
        replayTo ! ErrorReceived(channelId, List(new Exception(s"status: $publishedStatus.")))
      }

    case Fetch(channelId, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch received. state: ${state.size}. status: $publishedStatus."
        )
      }
      replayMsg(channelId, replayTo, state)

    case FetchIfNotRunning(channelId, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-if-not-running received. state: ${state.size}. status: $publishedStatus."
        )
      }
      if (running) {
        replayTo ! ErrorReceived(channelId, List(new Exception("update running.")))
      } else {
        replayMsg(channelId, replayTo, state)
      }

    case FetchWithKeyFilter(channelId, filter, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-with-key-filter. state: ${state.size}. status: $publishedStatus."
        )
      }
      replayKeyFilteredMsg(channelId, replayTo, state, filter)

    case FetchWithValueFilter(channelId, filter, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-with-value-filter received. state: ${state.size}. status: $publishedStatus."
        )
      }
      replayValueFilteredMsg(channelId, replayTo, state, filter)

    case FetchWithKeyValueFilter(channelId, conditionKey, conditionValue, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-with-key-value-filter received. state: ${state.size}. status: $publishedStatus."
        )
      }
      replayKeyValueFilteredMsg(channelId, replayTo, state, conditionKey, conditionValue)

    case FetchWithMap(channelId, map, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-filtered received. state: ${state.size}. status: $publishedStatus."
        )
      }
      val mapFn = map.asInstanceOf[Value => List[_]]
      replayMapMsg(channelId, replayTo, state, mapFn)
  }
}
