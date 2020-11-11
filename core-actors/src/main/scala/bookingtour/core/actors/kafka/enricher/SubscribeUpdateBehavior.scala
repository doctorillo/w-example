package bookingtour.core.actors.kafka.enricher

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.{ChannelCreate, ChannelDelete}
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.{SignalChannelCreate, SignalChannelDelete}
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeChannelError, EdgeConsumerChannelCreated}
import bookingtour.protocols.core.db.DbEventPayload
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import cats.instances.string._
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
protected[enricher] trait SubscribeUpdateBehavior[Value, DbId, Id, Stamp] {
  _: Actor with Stash with ActorLogging with State[Value, DbId, Id, Stamp] with BasicBehavior[Value, DbId, Id, Stamp] =>
  private final val tag = s"$uniqueTag. ch: update"

  private final def behaviors(
      channel: ActorProducer[Value, Id],
      truncateCh: EdgeConsumerChannelCreated
  ): Receive = {
    case msg: EdgeConsumerChannelCreated =>
      if (enableTrace) {
        log.info(s"$tag. consumer-channel-created.")
      }
      unstashAll()
      reload(
        sequenceId = SequenceNr.Zero,
        truncateCh = truncateCh,
        updateCh = msg,
        channel = channel,
        signals = List.empty,
        pending = List.empty,
        running = false,
        lastStatus = ChannelStatus.Undefined,
        breakerStatus = ChannelStatus.Ready
      )

    case EdgeChannelError(_, err, _) =>
      log.error(s"$tag. consumer-channel-created. $err")
      shutdown()

    case msg: ChannelCreate =>
      if (enableTrace) {
        log.info(
          s"$tag. channel-create. forward."
        )
      }
      channel.x.forward(msg)

    case msg: ChannelDelete =>
      if (enableTrace) {
        log.info(
          s"$tag. channel-delete. forward."
        )
      }
      channel.x.forward(msg)

    case msg: SignalChannelCreate =>
      if (enableTrace) {
        log.info(
          s"$tag. signal-channel-create. forward."
        )
      }
      channel.x.forward(msg)

    case msg: SignalChannelDelete =>
      if (enableTrace) {
        log.info(
          s"$tag. signal-channel-delete. forward."
        )
      }
      channel.x.forward(msg)

    case _ =>
      stash()
  }

  protected final def subscribeUpdateBehavior(
      channel: ActorProducer[Value, Id],
      truncateCh: EdgeConsumerChannelCreated
  ): Unit = {
    context.become(behaviors(channel, truncateCh))
    val msg = KafkaEdge.>.makeConsumer[DbEventPayload.BaseEntity[DbId, Stamp]](
      uniqueTag = uniqueTag,
      topic = topic,
      register = updateEntity,
      filter = _.table === table,
      dropBefore = dropBefore,
      replayTo = self
    )(self)
    pool.x ! msg
  }
}
