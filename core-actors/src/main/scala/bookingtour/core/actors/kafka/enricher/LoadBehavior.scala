package bookingtour.core.actors.kafka.enricher

import java.time.{Duration, Instant}

import akka.actor.{Actor, ActorLogging, Stash}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand._
import bookingtour.protocols.core.actors.channels.signal.ChannelSignalCommand.{SignalChannelCreate, SignalChannelDelete}
import bookingtour.protocols.core.db.DbAnswer

/**
  * © Alexey Toroshchin 2019.
  */
protected[enricher] trait LoadBehavior[Value, DbId, Id, Stamp] {
  _: Actor
    with Stash with ActorLogging with State[Value, DbId, Id, Stamp]
    with SubscribeTruncateBehavior[Value, DbId, Id, Stamp] =>

  private final val tag = s"$uniqueTag. load-behavior"

  private final def behaviors(channel: ActorProducer[Value, Id]): Receive = {
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

  protected final def loadBehavior(channel: ActorProducer[Value, Id]): Unit = {
    context.become(behaviors(channel))
    val start = Instant.now()
    if (enableTrace) {
      log.info(s"$tag. start loading.")
    }
    zioRuntime.unsafeRunAsync(readAll.use(_.ask)) {
      case zio.Exit.Failure(cause) =>
        cause.failures.foreach(thr => log.error(s"$tag. {}", thr))
        shutdown()
      case zio.Exit.Success(payload) =>
        payload match {
          case DbAnswer.Empty =>
            if (enableTrace) {
              log.info(s"$tag. receive empty.")
            }
            channel.x ! ChannelPushStatus(channelStateId, ChannelStatus.Undefined)
            subscribeTruncateBehavior(channel)

          case DbAnswer.NonEmpty(nec) =>
            if (enableTrace) {
              log.info(
                s"$tag. receive non empty (${nec.length}). ${Duration.between(start, Instant.now()).toMillis} ms."
              )
            }
            channel.x ! ChannelPushSnapshot(channelStateId, nec)
            channel.x ! ChannelPushStatus(channelStateId, ChannelStatus.Ready)
            subscribeTruncateBehavior(channel)
        }
    }
  }
}
