package bookingtour.core.actors.kafka.state.producer

import java.time.Instant
import java.util.UUID

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash}
import akka.event.LoggingAdapter
import akka.util.Timeout
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.{EdgeRef, KafkaEdgeWrapper}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.core.actors.channels.ChannelStatus
import bookingtour.protocols.core.actors.channels.basic.ChannelEvent.ChannelCreated
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelCommand.{
  DChannelCreate,
  DChannelDelete
}
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelEvent
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelEvent._
import bookingtour.protocols.core.actors.channels.query.ChannelFetchCommand.actors._
import bookingtour.protocols.core.actors.kafka.EdgeCommand
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeConsumerChannelCreated, EdgeProducerChannelCreated}
import bookingtour.protocols.core.actors.kafka.EdgeProducerCommand.EdgePublish
import bookingtour.protocols.core.messages.TaggedChannel.{ChannelSession, ChannelTag}
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import bookingtour.protocols.core.register.RegisterEntity
import cats.instances.all._
import cats.syntax.order._
import io.estatico.newtype.ops._
import zio.Exit

/**
  * © Alexey Toroshchin 2019.
  */
final class DStateProducer[Value, Id] private (
    val uniqueTag: String,
    val targetTag: String,
    val edgeRef: EdgeRef,
    val inputTopic: String,
    val dataLink: ActorProducer[Value, Id],
    val makeState: (
        ChannelCreated,
        EdgeConsumerChannelCreated,
        EdgeConsumerChannelCreated,
        LoggingAdapter
    ) => DStateProducer.State,
    val makeSubscription: (
        DChannelCreated,
        ActorRef
    ) => Unit,
    val ttlMs: Long,
    val enableTrace: Boolean
)(
    implicit val createEntity: RegisterEntity.Aux[DChannelCreate],
    val deleteEntity: RegisterEntity.Aux[DChannelDelete]
) extends Actor with Stash with ActorLogging with State[Value, Id] with BasicBehavior[Value, Id]
    with CreateDataChannelBehavior[Value, Id] with ChannelCreateBehavior[Value, Id]
    with ChannelDeleteBehavior[Value, Id] {
  override def preStart(): Unit = {
    super.preStart()
    createDataChannelBehavior()
  }

  override def postRestart(reason: Throwable): Unit = {
    super.postRestart(reason)
    log.error(s"$uniqueTag. post-restart. {}", reason)
    shutdown()
  }

  override def receive: Receive = Actor.emptyBehavior
}

object DStateProducer {
  final case class State(
      uniqueTag: String,
      stateChannel: ChannelCreated,
      createChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeConsumerChannelCreated,
      subscriptions: List[DStateProducer.SubscriptionChannel],
      status: ChannelStatus,
      enableTrace: Boolean,
      log: LoggingAdapter,
      zioRuntime: zio.Runtime[zio.ZEnv]
  ) {
    val expiredAt: () => Instant = () => Instant.now().plusSeconds(30)
    def channelStateId: UUID     = stateChannel.channelId
    def channelCreateId: UUID    = createChannel.id
    def channelDeleteId: UUID    = deleteChannel.id
    def deleteSubscription(id: UUID): State =
      copy(subscriptions = subscriptions.filterNot(_.subscription.sessionId === id))
    def deleteUnreachableTopic(topic: String): State = {
      val xs = subscriptions.filterNot(_.subscription.consumerTopic === topic)
      copy(subscriptions = xs)
    }
    private def makeChannel(session: DistributionChannelEvent.DChannelCreated): ChannelSession =
      ChannelSession(
        sessionId = session.sessionId,
        tag = session.targetTag
      )

    def publishCreated[Value](
        channel: DStateProducer.SubscriptionChannel
    ): State = {
      val sessionId  = channel.subscription.sessionId
      val dchannel   = makeChannel(channel.subscription)
      val createdId  = channel.subscription.sequenceId
      val snapshotId = createdId.next
      val statusId   = snapshotId.next
      val announceId = statusId.next
      channel.createdChannel.replayTo ! EdgePublish(
        id = channel.createdChannel.id,
        channel = makeChannel(channel.subscription),
        msg = channel.subscription.copy(sequenceId = createdId, announceSequenceId = snapshotId),
        expiredAt = expiredAt()
      )
      if (enableTrace) {
        log.info(
          s"$uniqueTag. tag: ${channel.subscription.targetTag}. send created to ${channel.subscription.consumerTopic}. sequence-id: $createdId. announce-sequence-id: $snapshotId."
        )
      }
      zioRuntime.unsafeRunAsync(
        askData[Value](
          producer = stateChannel.producer,
          id = stateChannel.channelId,
          tag = stateChannel.tag
        )
      ) {
        case Exit.Failure(cause) =>
          cause.failures.foreach(err => log.error(s"$uniqueTag. $err"))
        case Exit.Success(nec) if nec.isEmpty =>
          val dmsg = DChannelEmptySnapshotReceived(
            sessionId = sessionId,
            announceSequenceId = statusId,
            sequenceId = snapshotId
          )
          channel.emptySnapshotChannel.replayTo ! EdgePublish(
            id = channel.emptySnapshotChannel.id,
            channel = dchannel,
            msg = dmsg,
            expiredAt = expiredAt()
          )
          if (enableTrace) {
            log.info(
              s"$uniqueTag. tag: ${channel.subscription.targetTag}. send empty snapshot to ${channel.subscription.consumerTopic}. sequence-id: ${dmsg.sequenceId}."
            )
          }

        case Exit.Success(nec) =>
          val dmsg = DChannelSnapshotReceived(
            sessionId = sessionId,
            announceSequenceId = statusId,
            sequenceId = snapshotId,
            data = nec
          )
          channel.snapshotChannel.replayTo ! EdgePublish(
            id = channel.snapshotChannel.id,
            channel = dchannel,
            msg = dmsg,
            expiredAt = expiredAt()
          )
          if (enableTrace) {
            log.info(
              s"$uniqueTag. tag: ${channel.subscription.targetTag}. send snapshot(${nec.length}) to ${channel.subscription.consumerTopic}. sequence-id: ${dmsg.sequenceId}."
            )
          }
      }
      val dmsg =
        DChannelStatusChanged(
          sessionId = sessionId,
          announceSequenceId = announceId,
          sequenceId = statusId,
          status = status
        )
      channel.statusChangedChannel.replayTo ! EdgePublish(
        id = channel.statusChangedChannel.id,
        channel = dchannel,
        msg = dmsg,
        expiredAt = expiredAt()
      )
      if (enableTrace) {
        log.info(
          s"$uniqueTag. tag: ${channel.subscription.targetTag}. send status changed($status) to ${channel.subscription.consumerTopic}. sequence-id: ${dmsg.sequenceId}. announce-sequence-id: $announceId."
        )
      }
      copy(subscriptions = subscriptions :+ channel.copy(announceSequenceId = announceId))
    }

    def publishStatus(status: ChannelStatus): State = {
      val xs = for {
        c <- subscriptions
        _ = if (enableTrace) {
          log.info(
            s"$uniqueTag. tag: ${c.subscription.targetTag}. send status changed($status) to ${c.subscription.consumerTopic}. sequence-id: ${c.announceSequenceId}. announce-sequence-id: ${c.announceSequenceId.next}."
          )
        }
        _ = c.statusChangedChannel.replayTo ! EdgePublish(
          id = c.statusChangedChannel.id,
          channel = makeChannel(c.subscription),
          msg = DChannelStatusChanged(
            sessionId = c.subscription.sessionId,
            announceSequenceId = c.announceSequenceId.next,
            sequenceId = c.announceSequenceId,
            status = status
          ),
          expiredAt = expiredAt()
        )
      } yield c.nextAnnounce()
      copy(subscriptions = xs, status = status)
    }

    def publishEmptyState(): State = {
      val xs = for {
        c <- subscriptions
        _ = if (enableTrace) {
          log.info(
            s"$uniqueTag. tag: ${c.subscription.targetTag}. send empty snapshot to ${c.subscription.consumerTopic}. sequence-id: ${c.announceSequenceId}. announce-sequence-id: ${c.announceSequenceId.next}."
          )
        }
        _ = c.emptySnapshotChannel.replayTo ! EdgePublish(
          id = c.emptySnapshotChannel.id,
          channel = makeChannel(c.subscription),
          msg = DChannelEmptySnapshotReceived(
            sessionId = c.subscription.sessionId,
            announceSequenceId = c.announceSequenceId.next,
            sequenceId = c.announceSequenceId
          ),
          expiredAt = expiredAt()
        )
      } yield c.nextAnnounce()
      copy(subscriptions = xs)
    }

    def publishState[Value](data: List[Any]): State = {
      try {
        val v = data.asInstanceOf[List[Value]]
        val xs = for {
          c <- subscriptions
          _ = if (enableTrace) {
            log.info(
              s"$uniqueTag. tag: ${c.subscription.targetTag}. send snapshot(${v.length}) to ${c.subscription.consumerTopic}. sequence-id: ${c.announceSequenceId}. announce-sequence-id: ${c.announceSequenceId.next}."
            )
          }
          _ = c.snapshotChannel.replayTo ! EdgePublish(
            id = c.snapshotChannel.id,
            channel = makeChannel(c.subscription),
            msg = DChannelSnapshotReceived(
              sessionId = c.subscription.sessionId,
              announceSequenceId = c.announceSequenceId.next,
              sequenceId = c.announceSequenceId,
              data = v
            ),
            expiredAt = expiredAt()
          )
        } yield c.nextAnnounce()
        copy(subscriptions = xs)
      } catch {
        case thr: Throwable =>
          log.error(s"$uniqueTag. send snapshot. {}.", thr)
          this
      }
    }

    def publishItemCreated[Value](data: List[Any]): State = {
      try {
        val v = data.asInstanceOf[List[Value]]
        val xs = for {
          c <- subscriptions
          _ = if (enableTrace) {
            log.info(
              s"$uniqueTag. tag: ${c.subscription.targetTag}. send item created(${v.length}) to ${c.subscription.consumerTopic}. sequence-id: ${c.announceSequenceId}. announce-sequence-id: ${c.announceSequenceId.next}."
            )
          }
          _ = c.itemCreatedChannel.replayTo ! EdgePublish(
            id = c.itemCreatedChannel.id,
            channel = makeChannel(c.subscription),
            msg = DChannelItemCreated(
              sessionId = c.subscription.sessionId,
              announceSequenceId = c.announceSequenceId.next,
              sequenceId = c.announceSequenceId,
              data = v
            ),
            expiredAt = expiredAt()
          )
        } yield c.nextAnnounce()
        copy(subscriptions = xs)
      } catch {
        case thr: Throwable =>
          log.error(s"$uniqueTag. send item created. {}.", thr)
          this
      }
    }

    def publishItemUpdated[Value](data: List[Any]): State = {
      try {
        val v = data.asInstanceOf[List[Value]]
        val xs = for {
          c <- subscriptions
          _ = if (enableTrace) {
            log.info(
              s"$uniqueTag. tag: ${c.subscription.targetTag}. send item updated(${v.length}) to ${c.subscription.consumerTopic}. sequence-id: ${c.announceSequenceId}. announce-sequence-id: ${c.announceSequenceId.next}."
            )
          }
          _ = c.itemUpdatedChannel.replayTo ! EdgePublish(
            id = c.itemUpdatedChannel.id,
            channel = makeChannel(c.subscription),
            msg = DChannelItemUpdated(
              sessionId = c.subscription.sessionId,
              announceSequenceId = c.announceSequenceId.next,
              sequenceId = c.announceSequenceId,
              data = v
            ),
            expiredAt = expiredAt()
          )
        } yield c.nextAnnounce()
        copy(subscriptions = xs)
      } catch {
        case thr: Throwable =>
          log.error(s"$uniqueTag. send item updated. {}.", thr)
          this
      }
    }

    def publishItemDeleted[Value](data: List[Any]): State = {
      try {
        val v = data.asInstanceOf[List[Value]]
        val xs = for {
          c <- subscriptions
          _ = if (enableTrace) {
            log.info(
              s"$uniqueTag. tag: ${c.subscription.targetTag}. send item deleted(${v.length}) to ${c.subscription.consumerTopic}. sequence-id: ${c.announceSequenceId}. announce-sequence-id: ${c.announceSequenceId.next}."
            )
          }
          _ = c.itemDeletedChannel.replayTo ! EdgePublish(
            id = c.itemDeletedChannel.id,
            channel = makeChannel(c.subscription),
            msg = DChannelItemDeleted(
              sessionId = c.subscription.sessionId,
              announceSequenceId = c.announceSequenceId.next,
              sequenceId = c.announceSequenceId,
              data = v
            ),
            expiredAt = expiredAt()
          )
        } yield c.nextAnnounce()
        copy(subscriptions = xs)
      } catch {
        case thr: Throwable =>
          log.error(s"$uniqueTag. send item deleted. {}.", thr)
          this
      }
    }
  }

  final object State {
    def make(uniqueTag: String, enableTrace: Boolean, zioRuntime: zio.Runtime[zio.ZEnv])(
        stateChannel: ChannelCreated,
        createChannel: EdgeConsumerChannelCreated,
        deleteChannel: EdgeConsumerChannelCreated,
        log: LoggingAdapter
    ): State = State(
      uniqueTag = uniqueTag,
      stateChannel = stateChannel,
      createChannel = createChannel,
      deleteChannel = deleteChannel,
      subscriptions = List.empty,
      status = ChannelStatus.Undefined,
      enableTrace = enableTrace,
      log = log,
      zioRuntime = zioRuntime
    )
  }

  final case class SubscriptionChannel(
      subscription: DChannelCreated,
      createdChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeProducerChannelCreated,
      emptySnapshotChannel: EdgeProducerChannelCreated,
      snapshotChannel: EdgeProducerChannelCreated,
      itemCreatedChannel: EdgeProducerChannelCreated,
      itemUpdatedChannel: EdgeProducerChannelCreated,
      itemDeletedChannel: EdgeProducerChannelCreated,
      statusChangedChannel: EdgeProducerChannelCreated,
      announceSequenceId: SequenceNr
  ) {
    def nextAnnounce(): SubscriptionChannel = copy(announceSequenceId = announceSequenceId.next)
  }

  final object SubscriptionChannel {
    def make(
        subscription: DChannelCreated,
        createdChannel: EdgeProducerChannelCreated,
        deletedChannel: EdgeProducerChannelCreated,
        emptySnapshotChannel: EdgeProducerChannelCreated,
        snapshotChannel: EdgeProducerChannelCreated,
        itemCreatedChannel: EdgeProducerChannelCreated,
        itemUpdatedChannel: EdgeProducerChannelCreated,
        itemDeletedChannel: EdgeProducerChannelCreated,
        statusChangedChannel: EdgeProducerChannelCreated
    ): SubscriptionChannel = SubscriptionChannel(
      subscription: DChannelCreated,
      createdChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeProducerChannelCreated,
      emptySnapshotChannel: EdgeProducerChannelCreated,
      snapshotChannel: EdgeProducerChannelCreated,
      itemCreatedChannel: EdgeProducerChannelCreated,
      itemUpdatedChannel: EdgeProducerChannelCreated,
      itemDeletedChannel: EdgeProducerChannelCreated,
      statusChangedChannel: EdgeProducerChannelCreated,
      announceSequenceId = subscription.sequenceId
    )
  }

  private final def mk0[Value](
      uniqueTag: String,
      edgeRef: EdgeRef,
      targetTag: String,
      zioRuntime: zio.Runtime[zio.ZEnv],
      createEntity: RegisterEntity.Aux[DChannelCreate],
      createdEntity: RegisterEntity.Aux[DChannelCreated],
      deleteEntity: RegisterEntity.Aux[DChannelDelete],
      deletedEntity: RegisterEntity.Aux[DChannelDeleted],
      emptySnapshotReceivedEntity: RegisterEntity.Aux[
        DChannelEmptySnapshotReceived
      ],
      snapshotReceivedEntity: RegisterEntity.Aux[
        DChannelSnapshotReceived[Value]
      ],
      itemCreatedEntity: RegisterEntity.Aux[
        DChannelItemCreated[Value]
      ],
      itemUpdatedEntity: RegisterEntity.Aux[
        DChannelItemUpdated[Value]
      ],
      itemDeletedEntity: RegisterEntity.Aux[
        DChannelItemDeleted[Value]
      ],
      statusChangedEntity: RegisterEntity.Aux[
        DChannelStatusChanged
      ],
      timeoutMs: Long
  )(
      subscription: DChannelCreated,
      replayTo: ActorRef
  ): Unit = {
    implicit val timeout: Timeout = timeoutMs.seconds
    implicit val taggedChannel: ChannelTag = ChannelTag(
      tag = targetTag
    )
    val created = KafkaEdge.>.makeProducerChannel[DChannelCreated](
      uniqueTag = uniqueTag,
      topic = subscription.consumerTopic,
      register = createdEntity,
      replayTo = replayTo
    )
    val deleted = KafkaEdge.>.makeProducerChannel[DChannelDeleted](
      uniqueTag = uniqueTag,
      topic = subscription.consumerTopic,
      register = deletedEntity,
      replayTo = replayTo
    )
    val emptySnapshot = KafkaEdge.>.makeProducerChannel[DChannelEmptySnapshotReceived](
      uniqueTag = uniqueTag,
      topic = subscription.consumerTopic,
      register = emptySnapshotReceivedEntity,
      replayTo = replayTo
    )
    val snapshot = KafkaEdge.>.makeProducerChannel[DChannelSnapshotReceived[Value]](
      uniqueTag = uniqueTag,
      topic = subscription.consumerTopic,
      register = snapshotReceivedEntity,
      replayTo = replayTo
    )
    val itemCreated = KafkaEdge.>.makeProducerChannel[DChannelItemCreated[Value]](
      uniqueTag = uniqueTag,
      topic = subscription.consumerTopic,
      register = itemCreatedEntity,
      replayTo = replayTo
    )
    val itemUpdated = KafkaEdge.>.makeProducerChannel[DChannelItemUpdated[Value]](
      uniqueTag = uniqueTag,
      topic = subscription.consumerTopic,
      register = itemUpdatedEntity,
      replayTo = replayTo
    )
    val itemDeleted = KafkaEdge.>.makeProducerChannel[DChannelItemDeleted[Value]](
      uniqueTag = uniqueTag,
      topic = subscription.consumerTopic,
      register = itemDeletedEntity,
      replayTo = replayTo
    )
    val statusChanged = KafkaEdge.>.makeProducerChannel[DChannelStatusChanged](
      uniqueTag = uniqueTag,
      topic = subscription.consumerTopic,
      register = statusChangedEntity,
      replayTo = replayTo
    )
    val effect = for {
      createdF <- EdgeCommand.>.makeProducer(uniqueTag = uniqueTag, edgeRef = edgeRef.x, created).fork
      deletedF <- EdgeCommand.>.makeProducer(uniqueTag = uniqueTag, edgeRef = edgeRef.x, deleted).fork
      emptySnapshotF <- EdgeCommand.>.makeProducer(
                         uniqueTag = uniqueTag,
                         edgeRef = edgeRef.x,
                         emptySnapshot
                       ).fork
      snapshotF <- EdgeCommand.>.makeProducer(uniqueTag = uniqueTag, edgeRef = edgeRef.x, snapshot).fork
      itemCreatedF <- EdgeCommand.>.makeProducer(
                       uniqueTag = uniqueTag,
                       edgeRef = edgeRef.x,
                       itemCreated
                     ).fork
      itemUpdatedF <- EdgeCommand.>.makeProducer(
                       uniqueTag = uniqueTag,
                       edgeRef = edgeRef.x,
                       itemUpdated
                     ).fork
      itemDeletedF <- EdgeCommand.>.makeProducer(
                       uniqueTag = uniqueTag,
                       edgeRef = edgeRef.x,
                       itemDeleted
                     ).fork
      statusChangedF <- EdgeCommand.>.makeProducer(
                         uniqueTag = uniqueTag,
                         edgeRef = edgeRef.x,
                         statusChanged
                       ).fork
      createdP       <- createdF.join
      deletedP       <- deletedF.join
      emptySnapshotP <- emptySnapshotF.join
      snapshotP      <- snapshotF.join
      itemCreatedP   <- itemCreatedF.join
      itemUpdatedP   <- itemUpdatedF.join
      itemDeletedP   <- itemDeletedF.join
      statusChangedP <- statusChangedF.join
    } yield DStateProducer.SubscriptionChannel.make(
      subscription = subscription,
      createdChannel = createdP,
      deletedChannel = deletedP,
      emptySnapshotChannel = emptySnapshotP,
      snapshotChannel = snapshotP,
      itemCreatedChannel = itemCreatedP,
      itemUpdatedChannel = itemUpdatedP,
      itemDeletedChannel = itemDeletedP,
      statusChangedChannel = statusChangedP
    )
    zioRuntime.unsafeRunAsync(effect) {
      case Exit.Failure(cause) =>
        cause.failures.foreach(err => println(s"$uniqueTag. $err."))

      case Exit.Success(channel) =>
        replayTo ! channel
    }
  }

  final def make[Value, Id](
      uniqueTag: String,
      targetTag: String,
      inputTopic: String,
      dataLink: ActorProducer[Value, Id],
      ttl: Long,
      enableTrace: Boolean,
      timeoutSec: Long = 30L
  )(
      implicit ctx: ActorSystem,
      zioRuntime: zio.Runtime[zio.ZEnv],
      consumerWrapper: KafkaEdgeWrapper,
      createEntity: RegisterEntity.Aux[DChannelCreate],
      createdEntity: RegisterEntity.Aux[DChannelCreated],
      deleteEntity: RegisterEntity.Aux[DChannelDelete],
      deletedEntity: RegisterEntity.Aux[DChannelDeleted],
      emptySnapshotReceivedEntity: RegisterEntity.Aux[
        DChannelEmptySnapshotReceived
      ],
      snapshotReceivedEntity: RegisterEntity.Aux[
        DChannelSnapshotReceived[Value]
      ],
      itemCreatedEntity: RegisterEntity.Aux[
        DChannelItemCreated[Value]
      ],
      itemUpdatedEntity: RegisterEntity.Aux[
        DChannelItemUpdated[Value]
      ],
      itemDeletedEntity: RegisterEntity.Aux[
        DChannelItemDeleted[Value]
      ],
      statusChangedEntity: RegisterEntity.Aux[
        DChannelStatusChanged
      ]
  ): ActorProducer[Value, Id] = {
    val a: (
        ChannelCreated,
        EdgeConsumerChannelCreated,
        EdgeConsumerChannelCreated,
        LoggingAdapter
    ) => State = State.make(uniqueTag, enableTrace, zioRuntime)
    val f: (DChannelCreated, ActorRef) => Unit =
      mk0[Value](
        uniqueTag = uniqueTag,
        edgeRef = consumerWrapper.pool,
        targetTag = targetTag,
        zioRuntime = zioRuntime,
        createEntity = createEntity,
        createdEntity = createdEntity,
        deleteEntity = deleteEntity,
        deletedEntity = deletedEntity,
        emptySnapshotReceivedEntity = emptySnapshotReceivedEntity,
        snapshotReceivedEntity = snapshotReceivedEntity,
        itemCreatedEntity = itemCreatedEntity,
        itemUpdatedEntity = itemUpdatedEntity,
        itemDeletedEntity = itemDeletedEntity,
        statusChangedEntity = statusChangedEntity,
        timeoutMs = timeoutSec
      )
    ctx
      .actorOf(
        Props(
          new DStateProducer(
            uniqueTag = uniqueTag,
            targetTag = targetTag,
            edgeRef = consumerWrapper.pool,
            inputTopic = inputTopic,
            dataLink = dataLink,
            makeState = a,
            makeSubscription = f,
            ttlMs = ttl,
            enableTrace = enableTrace
          )
        ),
        uniqueTag
      )
      .coerce[ActorProducer[Value, Id]]
  }
}
