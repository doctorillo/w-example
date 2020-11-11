package bookingtour.core.actors.primitives.channel.filtered

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.ChannelDelete
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent._
import bookingtour.protocols.core.actors.channels.query.ChannelFetchCommand.{
  Fetch,
  FetchStatus,
  FetchWithMap,
  FetchWithValueFilter
}
import bookingtour.protocols.core.actors.channels.query.ChannelFetchEvent.{
  AnswerReceived,
  EmptyReceived,
  StatusReceived
}
import bookingtour.protocols.core.actors.operations.OpCommand.Stop
import cats.instances.uuid._
import cats.syntax.eq._

/**
  * © Alexey Toroshchin 2019.
  */
protected[filtered] trait BasicBehavior[K, IN, ID] {
  _: Actor with ActorLogging with State[K, IN, ID] =>

  protected final def basicBehavior(
      channel: ChannelCreated,
      state: List[IN] = List.empty,
      status: ChannelStatus
  ): Unit =
    context.become(
      behaviors(
        channel = channel,
        state = state,
        status = status
      )
    )

  private final def behaviors(
      channel: ChannelCreated,
      state: List[IN],
      status: ChannelStatus
  ): Receive = {
    case ChannelStatusChanged(id, s) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-status-changed-received."
        )
      }
      basicBehavior(
        channel = channel,
        state = state,
        status = s
      )

    case ChannelEmptySnapshotReceived(id) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-empty-snapshot-received."
        )
      }
      basicBehavior(
        channel = channel,
        state = state,
        status = ChannelStatus.Undefined
      )

    case ChannelSnapshotReceived(id, data) if id === channel.channelId =>
      val nec = data.asInstanceOf[List[IN]].filter(select)
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-snapshot-received. data: ${nec.length} items. state: ${state.length}."
        )
      }
      basicBehavior(
        channel = channel,
        state = nec,
        status = status
      )

    case ChannelItemUpdated(id, data) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-updated-received. data: ${data.length} items. state: ${state.length}."
        )
      }
      val nec = data.asInstanceOf[List[IN]].filter(select)
      val _state = state.filterNot { x =>
        val _id = chR(x)
        nec.exists(z => chR(z) === _id)
      }
      basicBehavior(
        channel = channel,
        state = _state,
        status = status
      )

    case ChannelItemDeleted(id, data) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. channel-deleted-received. data: ${data.length} items. state: ${state.length}."
        )
      }
      val nec = data.asInstanceOf[List[IN]].filter(select)
      val _state = state.filterNot { x =>
        val _id = chR(x)
        nec.exists(z => chR(z) === _id)
      }
      basicBehavior(
        channel = channel,
        state = _state,
        status = ChannelStatus.Ready
      )

    case FetchStatus(channelId, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-status received. state: ${state.length}. status: $status."
        )
      }
      replayTo ! StatusReceived(channelId, status)

    case Fetch(channelId, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch received. state: ${state.length}. status: $status."
        )
      }
      if (state.isEmpty) {
        replayTo ! EmptyReceived(channelId)
      } else {
        replayTo ! AnswerReceived(channelId, state)
      }

    case FetchWithValueFilter(channelId, filter, replayTo) =>
      val filterFn = filter.asInstanceOf[IN => Boolean]
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-filtered received. state: ${state.length}. status: $status."
        )
      }
      val data = state.filter(filterFn)
      if (data.isEmpty) {
        replayTo ! EmptyReceived(channelId)
      } else {
        replayTo ! AnswerReceived(channelId, data)
      }

    case FetchWithMap(channelId, map, replayTo) =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. fetch-filtered received. state: ${state.length}. status: $status."
        )
      }
      val mapFn = map.asInstanceOf[IN => List[_]]
      val data  = state.flatMap(mapFn(_))
      if (data.isEmpty) {
        replayTo ! EmptyReceived(channelId)
      } else {
        replayTo ! AnswerReceived(channelId, data)
      }

    case Stop =>
      if (enableTrace) {
        log.info(s"$uniqueTag. stop-received.")
      }
      channel.producer ! ChannelDelete(channel.channelId, channel.consumer)
      shutdown()

    case msg =>
      log.error(s"$uniqueTag. basic-behavior. unhandled $msg")
      shutdown()
  }
}
