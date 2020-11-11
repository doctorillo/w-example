package bookingtour.core.actors.kafka.state.producer

import java.time.Instant

import akka.actor.{Actor, ActorLogging, ActorRef}
import akka.event.LoggingAdapter
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.EdgeRef
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelCommand.{
  DChannelCreate,
  DChannelDelete
}
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelEvent._
import bookingtour.protocols.core.actors.kafka.EdgeEvent.EdgeConsumerChannelCreated
import bookingtour.protocols.core.messages.TaggedChannel.{ChannelSession, ChannelTag}
import bookingtour.protocols.core.messages._
import bookingtour.protocols.core.register.RegisterEntity

/**
  * © Alexey Toroshchin 2019.
  */
protected[producer] trait State[Value, Id] {
  _: Actor with ActorLogging =>

  val uniqueTag: String
  val edgeRef: EdgeRef
  val targetTag: String
  val inputTopic: String
  val dataLink: ActorProducer[Value, Id]
  val makeState: (
      ChannelCreated,
      EdgeConsumerChannelCreated,
      EdgeConsumerChannelCreated,
      LoggingAdapter
  ) => DStateProducer.State
  val makeSubscription: (
      DChannelCreated,
      ActorRef
  ) => Unit
  val ttlMs: Long
  val enableTrace: Boolean

  val createEntity: RegisterEntity.Aux[DChannelCreate]
  val deleteEntity: RegisterEntity.Aux[DChannelDelete]

  protected val dropBefore: Instant    = Instant.now()
  protected val postOffice: PostOffice = PostOffice(uniqueTag)

  implicit protected final val taggedChannel: ChannelTag = ChannelTag(
    tag = targetTag
  )

  protected final def expiredAt(): Instant = Instant.now().plusMillis(ttlMs)

  implicit protected final val filter: Value => Boolean = _ => true

  protected final def makeChannel(s: DChannelCreated): ChannelSession = ChannelSession(
    sessionId = s.sessionId,
    tag = s.targetTag
  )

  /*protected final def onChannelCreated(
    sequenceId: SequenceId,
    state: ChannelCreated,
    snapshotChannel: EdgeProducerChannelCreated,
    statusChangedChannel: EdgeProducerChannelCreated,
    consumers: NonEmptyChain[DChannelCreated]
  ): Unit = {
    val tag    = s"$uniqueTag. on-channel-created"
    val effect = askReadyNonEmptyData[Value](state.producer, state.channelId, state.tag)
    zioRuntime.unsafeRunAsync(effect) {
      case zio.Exit.Failure(cause) =>
        cause.failures.foreach(err => log.error(s"$tag. $err."))

      case zio.Exit.Success(nec) =>
        if (enableTrace) {
          log.info(s"$tag. items: ${nec.length}.")
        }
        for {
          c @ DChannelCreated(sessionId, _, _, _, consumerTopic) <- consumers
        } yield {
          snapshotChannel.replayTo ! EdgePublish(
            id = snapshotChannel.id,
            channel = makeChannel(c),
            DChannelSnapshotReceived(
              sessionId = sessionId,
              prevSequenceId = sequenceId.prev,
              sequenceId = sequenceId,
              data = nec
            ),
            expiredAt = expiredAt()
          )
          statusChangedChannel.replayTo ! EdgePublish(
            id = statusChangedChannel.id,
            channel = makeChannel(c),
            DChannelStatusChanged(
              sessionId = sessionId,
              sequenceId = sequenceId,
              status = ChannelStatus.Ready
            ),
            expiredAt = expiredAt()
          )
        }
    }
  }*/

  protected final def shutdown(): Unit = {
    if (enableTrace) {
      log.info(s"$uniqueTag. shutdown.")
    }
    context.stop(self)
  }
}
