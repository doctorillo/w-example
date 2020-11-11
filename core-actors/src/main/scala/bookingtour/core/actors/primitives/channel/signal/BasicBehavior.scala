package bookingtour.core.actors.primitives.channel.signal

import akka.actor.{Actor, ActorLogging}
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.SignalChannelDelete
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalEvent._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalMutationCommand.SignalChannelDequeueMutations
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalMutationEvent.SignalChannelMutationReceived
import bookingtour.protocols.core.actors.operations.OpCommand.Stop
import cats.instances.uuid._
import cats.syntax.eq._

/**
  * © Alexey Toroshchin 2019.
  */
protected[signal] trait BasicBehavior[Id] {
  _: Actor with ActorLogging with State[Id] =>

  protected final def basicBehavior(
      channel: SignalChannelCreated,
      status: ChannelStatus
  ): Unit =
    context.become(
      behaviors(
        channel = channel,
        status = status
      )
    )

  private final def behaviors(
      channel: SignalChannelCreated,
      status: ChannelStatus
  ): Receive = {
    case Stop =>
      if (enableTrace) {
        log.info(s"$uniqueTag. stop received.")
      }
      channel.producer ! SignalChannelDelete(channel.channelId, channel.consumer)

    case SignalChannelDeleted(id) if id === channel.channelId =>
      if (enableTrace) {
        log.info(
          s"$uniqueTag. signal-channel-deleted received received."
        )
      }
      shutdown()

    case SignalChannelStatusChanged(id, s) if id === channel.channelId =>
      if (enableTrace) {
        log.info(s"$uniqueTag. signal-channel-status-changed received. data: $s. status: $status.")
      }
      basicBehavior(
        channel = channel,
        status = s
      )
      manager ! SignalChannelStatusChanged(channelId, s)

    case SignalChannelDequeueMutations(id, replayTo) =>
      if (enableTrace) {
        log.info(s"$uniqueTag. signal-channel-dequeue-mutations received. status: $status.")
      }
      replayTo ! SignalChannelMutationReceived(
        channelId = id,
        status = status,
        producer = channel.producer
      )

    case msg =>
      log.error(s"$uniqueTag. basic-behavior. unhandled $msg")
      shutdown()
  }
}
