package bookingtour.core.actors.kafka.state.consumer

import java.time.Instant
import java.util.UUID

import scala.concurrent.duration._

import akka.actor.{Actor, ActorLogging, ActorRef, Stash, Timers}
import akka.event.LoggingAdapter
import akka.util.Timeout
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge
import bookingtour.core.actors.kafka.pool.edge.KafkaEdge.{EdgeRef, KafkaEdgeWrapper}
import bookingtour.protocols.actors.ActorProducer
import bookingtour.protocols.actors.channels.MakeChannel
import bookingtour.protocols.core._
import bookingtour.protocols.core.actors.channels.basic.ChannelCommand.{
  ChannelPushCreate,
  ChannelPushDelete,
  ChannelPushEmptySnapshot,
  ChannelPushSnapshot,
  ChannelPushStatus,
  ChannelPushUpdate
}
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelCommand.{
  DChannelCreate,
  DChannelDelete
}
import bookingtour.protocols.core.actors.channels.distribution.DistributionChannelEvent._
import bookingtour.protocols.core.actors.kafka.EdgeCommand
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{EdgeConsumerChannelCreated, EdgeProducerChannelCreated}
import bookingtour.protocols.core.actors.kafka.EdgeProducerCommand.EdgePublish
import bookingtour.protocols.core.messages.TaggedChannel
import bookingtour.protocols.core.messages.TaggedChannel.ChannelSession
import bookingtour.protocols.core.newtypes.quantities.SequenceNr
import bookingtour.protocols.core.register.RegisterEntity
import cats.Order
import cats.instances.all._
import cats.syntax.order._
import io.estatico.newtype.ops._
import zio.Exit

/**
  * © Alexey Toroshchin 2019.
  */
final class DStateConsumer[Value, Id] private (
    val uniqueTag: String,
    channelStateId: UUID,
    val outputTopic: String,
    channelFactory: MakeChannel[Value, Id],
    subscriptionFactory: (ActorRef, Exit[String, DStateConsumer.SubscriptionChannel] => Unit) => Unit,
    stateFactory: (
        DStateConsumer.SubscriptionChannel,
        ActorProducer[Value, Id],
        LoggingAdapter
    ) => DStateConsumer.State[Value, Id],
    val connectTimeoutSeconds: Long,
    val enableTrace: Boolean
) extends Actor with Stash with Timers with ActorLogging with State[Value, Id] with BasicBehavior[Value, Id] {
  override def preStart(): Unit = {
    super.preStart()
    val ref: ActorProducer[Value, Id] = channelFactory.make(
      ctx = context,
      managerRef = self,
      managerChannelId = channelStateId,
      uniqueTag = channelStateTag
    )
    subscriptionFactory(
      self, {
        case Exit.Failure(cause) =>
          cause.failures.foreach(err => log.error(s"$uniqueTag. $err"))
          shutdown()

        case Exit.Success(subscription) =>
          unstashAll()
          val state = stateFactory(subscription, ref, log).publishCreate()
          basicBehavior(state)
      }
    )
  }

  override def receive: Receive = {
    case _ =>
      stash()
  }
}

object DStateConsumer {
  import akka.actor.{ActorSystem, Props}
  import bookingtour.protocols.actors.channels.MakeChannel

  final case class SubscriptionChannel(
      createChannel: EdgeProducerChannelCreated,
      createdChannel: EdgeConsumerChannelCreated,
      deleteChannel: EdgeProducerChannelCreated,
      deletedChannel: EdgeConsumerChannelCreated,
      emptySnapshotChannel: EdgeConsumerChannelCreated,
      snapshotChannel: EdgeConsumerChannelCreated,
      itemCreatedChannel: EdgeConsumerChannelCreated,
      itemUpdatedChannel: EdgeConsumerChannelCreated,
      itemDeletedChannel: EdgeConsumerChannelCreated,
      statusChangedChannel: EdgeConsumerChannelCreated
  )

  final case class State[Value, Id](
      uniqueTag: String,
      stateChannelId: UUID,
      taggedChannel: ChannelSession,
      inputTopic: String,
      stateChannel: ActorProducer[Value, Id],
      subscription: DStateConsumer.SubscriptionChannel,
      buffer: List[(SequenceNr, () => Unit)],
      announcedId: SequenceNr,
      ttlSeconds: Long,
      enableTrace: Boolean,
      log: LoggingAdapter
  ) {
    implicit private val so: Ordering[SequenceNr] = implicitly[Order[SequenceNr]].toOrdering
    def channelCreateId: UUID                     = subscription.createChannel.id
    def channelCreatedId: UUID                    = subscription.createdChannel.id
    def channelDeleteId: UUID                     = subscription.deleteChannel.id
    def channelDeletedId: UUID                    = subscription.deletedChannel.id
    def channelEmptySnapshotId: UUID              = subscription.emptySnapshotChannel.id
    def channelSnapshotId: UUID                   = subscription.snapshotChannel.id
    def channelItemCreatedId: UUID                = subscription.itemCreatedChannel.id
    def channelItemUpdatedId: UUID                = subscription.itemUpdatedChannel.id
    def channelItemDeletedId: UUID                = subscription.itemDeletedChannel.id
    def channelStatusChangedId: UUID              = subscription.statusChangedChannel.id

    def publishCreate(): State[Value, Id] = {
      subscription.createChannel.replayTo ! EdgePublish(
        id = channelCreateId,
        channel = taggedChannel,
        DChannelCreate(
          sessionId = taggedChannel.sessionId,
          targetTag = taggedChannel.tag,
          consumerTopic = inputTopic
        ),
        expiredAt = Instant.now().plusSeconds(ttlSeconds)
      )
      if (enableTrace) {
        log.info(s"$uniqueTag. publish d-channel-create(${taggedChannel.tag}, $inputTopic)")
      }
      copy(announcedId = SequenceNr.Zero)
    }

    private def bufferEffect(
        announceId: SequenceNr,
        fix: (SequenceNr, () => Unit),
        source: List[(SequenceNr, () => Unit)]
    ): (SequenceNr, List[(SequenceNr, () => Unit)]) = {
      val target = (source :+ fix).sortBy(_._1)
      val sxs = target
        .foldLeft((List.empty[(SequenceNr, () => Unit)], announceId, true)) { (acc, x) =>
          val nextId = x._1.next
          if (acc._1.isEmpty && announceId =!= x._1) {
            (acc._1, acc._2, false)
          } else if (acc._3 && acc._2 === x._1) {
            (acc._1 :+ x, nextId, true)
          } else if (acc._3 && acc._2 =!= x._1) {
            (acc._1, acc._2, false)
          } else {
            acc
          }
        }
      val (effxs, announceSeqId, _) = sxs
      effxs.foreach(x => x._2())
      (announceSeqId, target.diff(sxs._1))
    }

    def receivedCreated(msg: DChannelCreated): State[Value, Id] = {
      val tag = s"$uniqueTag. tag: ${taggedChannel.tag}. d-channel-created"
      val effect = () => {
        if (enableTrace) {
          log.info(s"$tag. complete. sequence-id: ${msg.sequenceId}.")
        }
      }
      val (announceId, buf) = bufferEffect(announcedId, (msg.sequenceId, effect), buffer)
      copy(announcedId = announceId, buffer = buf)
    }

    def receivedEmptySnapshot(msg: DChannelEmptySnapshotReceived): State[Value, Id] = {
      val tag = s"$uniqueTag. tag: ${taggedChannel.tag}. d-channel-empty-snapshot-received"
      val effect = () => {
        if (enableTrace) {
          log.info(s"$tag. push empty snapshot. sequence-id: ${msg.sequenceId}.")
        }
        stateChannel.x ! ChannelPushEmptySnapshot(channelId = stateChannelId)
      }
      val (announceId, buf) = bufferEffect(announcedId, (msg.sequenceId, effect), buffer)
      copy(announcedId = announceId, buffer = buf)
    }

    def receivedSnapshot(msg: DChannelSnapshotReceived[Value]): State[Value, Id] = {
      val tag = s"$uniqueTag. tag: ${taggedChannel.tag}. d-channel-snapshot-received"
      val effect = () => {
        if (enableTrace) {
          log.info(s"$tag. push snapshot(${msg.data.length}). sequence-id: ${msg.sequenceId}.")
        }
        stateChannel.x ! ChannelPushSnapshot(channelId = stateChannelId, data = msg.data)
      }
      val (announceId, buf) = bufferEffect(announcedId, (msg.sequenceId, effect), buffer)
      copy(announcedId = announceId, buffer = buf)
    }

    def receivedItemCreated(msg: DChannelItemCreated[Value]): State[Value, Id] = {
      val tag = s"$uniqueTag. tag: ${taggedChannel.tag}. d-channel-item-created"
      val effect = () => {
        if (enableTrace) {
          log.info(s"$tag. push create(${msg.data.length}). sequence-id: ${msg.sequenceId}.")
        }
        stateChannel.x ! ChannelPushCreate(channelId = stateChannelId, data = msg.data)
      }
      val (announceId, buf) = bufferEffect(announcedId, (msg.sequenceId, effect), buffer)
      copy(announcedId = announceId, buffer = buf)
    }

    def receivedItemUpdated(msg: DChannelItemUpdated[Value]): State[Value, Id] = {
      val tag = s"$uniqueTag. tag: ${taggedChannel.tag}. d-channel-item-updated"
      val effect = () => {
        if (enableTrace) {
          log.info(s"$tag. push update(${msg.data.length}). sequence-id: ${msg.sequenceId}.")
        }
        stateChannel.x ! ChannelPushUpdate(channelId = stateChannelId, data = msg.data)
      }
      val (announceId, buf) = bufferEffect(announcedId, (msg.sequenceId, effect), buffer)
      copy(announcedId = announceId, buffer = buf)
    }

    def receivedItemDeleted(msg: DChannelItemDeleted[Value]): State[Value, Id] = {
      val tag = s"$uniqueTag. tag: ${taggedChannel.tag}. d-channel-item-deleted"
      val effect = () => {
        if (enableTrace) {
          log.info(s"$tag. push delete(${msg.data.length}). sequence-id: ${msg.sequenceId}.")
        }
        stateChannel.x ! ChannelPushDelete(channelId = stateChannelId, data = msg.data)
      }
      val (announceId, buf) = bufferEffect(announcedId, (msg.sequenceId, effect), buffer)
      copy(announcedId = announceId, buffer = buf)
    }

    def receivedStatusChanged(msg: DChannelStatusChanged): State[Value, Id] = {
      val tag = s"$uniqueTag. tag: ${taggedChannel.tag}. d-channel-status-changed"
      val effect = () => {
        if (enableTrace) {
          log.info(s"$tag. push status(${msg.status}). sequence-id: ${msg.sequenceId}.")
        }
        stateChannel.x ! ChannelPushStatus(channelId = stateChannelId, status = msg.status)
      }
      val (announceId, buf) = bufferEffect(announcedId, (msg.sequenceId, effect), buffer)
      copy(announcedId = announceId, buffer = buf)
    }
  }

  final object State {
    def make[Value, Id](
        uniqueTag: String,
        stateChannelId: UUID,
        taggedChannel: ChannelSession,
        inputTopic: String,
        ttlSeconds: Long,
        enableTrace: Boolean
    )(
        edgeSubscription: DStateConsumer.SubscriptionChannel,
        stateChannel: ActorProducer[Value, Id],
        log: LoggingAdapter
    ): State[Value, Id] =
      State[Value, Id](
        uniqueTag = uniqueTag,
        stateChannelId = stateChannelId,
        taggedChannel = taggedChannel,
        inputTopic = inputTopic,
        stateChannel = stateChannel,
        subscription = edgeSubscription,
        buffer = List.empty,
        announcedId = SequenceNr.Zero,
        ttlSeconds = ttlSeconds,
        enableTrace = enableTrace,
        log = log
      )
  }

  final def make[Value, Id](
      uniqueTag: String,
      targetTag: String,
      outputTopic: String,
      inputTopic: String,
      channelFactory: MakeChannel[Value, Id],
      ttl: Long,
      connectTimeoutSeconds: Long,
      enableTrace: Boolean = false
  )(
      implicit ctx: ActorSystem,
      wrapper: KafkaEdgeWrapper,
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
      ]
  ): ActorProducer[Value, Id] = {
    implicit val taggedChannel: ChannelSession =
      ChannelSession(sessionId = UUID.randomUUID(), tag = targetTag)
    val mkSubscription: (ActorRef, Exit[String, SubscriptionChannel] => Unit) => Unit =
      >.makeSubscriptionChannel[Value](
        uniqueTag = uniqueTag,
        edgeRef = wrapper.pool,
        outputTopic = outputTopic,
        inputTopic = inputTopic,
        zioRuntime = zioRuntime,
        taggedChannel = taggedChannel,
        createEntity = createEntity,
        createdEntity = createdEntity,
        deleteEntity = deleteEntity,
        deletedEntity = deletedEntity,
        emptySnapshotEntity = emptySnapshotReceivedEntity,
        snapshotEntity = snapshotReceivedEntity,
        itemCreatedEntity = itemCreatedEntity,
        itemUpdatedEntity = itemUpdatedEntity,
        itemDeletedEntity = itemDeletedEntity,
        statusChangedEntity = statusChangedEntity
      )
    val channelStateId: UUID = UUID.randomUUID()
    val mkState: (SubscriptionChannel, ActorProducer[Value, Id], LoggingAdapter) => State[Value, Id] =
      State.make[Value, Id](
        uniqueTag,
        stateChannelId = channelStateId,
        taggedChannel = taggedChannel,
        inputTopic = inputTopic,
        ttlSeconds = ttl,
        enableTrace = enableTrace
      )
    ctx
      .actorOf(
        Props(
          new DStateConsumer(
            uniqueTag = uniqueTag,
            outputTopic = outputTopic,
            channelStateId = channelStateId,
            channelFactory = channelFactory,
            subscriptionFactory = mkSubscription,
            stateFactory = mkState,
            connectTimeoutSeconds = connectTimeoutSeconds,
            enableTrace = enableTrace
          )
        ),
        uniqueTag
      )
      .coerce[ActorProducer[Value, Id]]
  }

  final object > {
    def makeSubscriptionChannel[Value](
        uniqueTag: String,
        edgeRef: EdgeRef,
        outputTopic: String,
        inputTopic: String,
        zioRuntime: zio.Runtime[zio.ZEnv],
        taggedChannel: TaggedChannel,
        createEntity: RegisterEntity.Aux[DChannelCreate],
        createdEntity: RegisterEntity.Aux[DChannelCreated],
        deleteEntity: RegisterEntity.Aux[DChannelDelete],
        deletedEntity: RegisterEntity.Aux[DChannelDeleted],
        emptySnapshotEntity: RegisterEntity.Aux[DChannelEmptySnapshotReceived],
        snapshotEntity: RegisterEntity.Aux[DChannelSnapshotReceived[Value]],
        itemCreatedEntity: RegisterEntity.Aux[
          DChannelItemCreated[Value]
        ],
        itemUpdatedEntity: RegisterEntity.Aux[DChannelItemUpdated[Value]],
        itemDeletedEntity: RegisterEntity.Aux[DChannelItemDeleted[Value]],
        statusChangedEntity: RegisterEntity.Aux[DChannelStatusChanged]
    )(replayTo: ActorRef, cb: Exit[String, SubscriptionChannel] => Unit): Unit = {
      implicit val tc: TaggedChannel = taggedChannel
      implicit val timeout: Timeout  = 30.seconds
      def allTrue[A]: A => Boolean   = _ => true
      val create = KafkaEdge.>.makeProducerChannel[DChannelCreate](
        uniqueTag = uniqueTag,
        topic = outputTopic,
        register = createEntity,
        replayTo = replayTo
      )
      val created: ActorRef => EdgeCommand.EdgeConsumerCreateWrapper[DChannelCreated] =
        KafkaEdge.>.makeConsumerChannel[DChannelCreated](
          uniqueTag = uniqueTag,
          topic = inputTopic,
          register = createdEntity,
          filter = allTrue,
          replayTo = replayTo
        )
      val delete = KafkaEdge.>.makeProducerChannel[DChannelDelete](
        uniqueTag = uniqueTag,
        topic = outputTopic,
        register = deleteEntity,
        replayTo = replayTo
      )
      val deleted = KafkaEdge.>.makeConsumerChannel[DChannelDeleted](
        uniqueTag = uniqueTag,
        topic = inputTopic,
        register = deletedEntity,
        filter = allTrue,
        replayTo = replayTo
      )
      val emptySnapshot = KafkaEdge.>.makeConsumerChannel[DChannelEmptySnapshotReceived](
        uniqueTag = uniqueTag,
        topic = inputTopic,
        register = emptySnapshotEntity,
        filter = allTrue,
        replayTo = replayTo
      )
      val snapshot = KafkaEdge.>.makeConsumerChannel[DChannelSnapshotReceived[Value]](
        uniqueTag = uniqueTag,
        topic = inputTopic,
        register = snapshotEntity,
        filter = allTrue,
        replayTo = replayTo
      )
      val itemCreated = KafkaEdge.>.makeConsumerChannel[DChannelItemCreated[Value]](
        uniqueTag = uniqueTag,
        topic = inputTopic,
        register = itemCreatedEntity,
        filter = allTrue,
        replayTo = replayTo
      )
      val itemUpdated = KafkaEdge.>.makeConsumerChannel[DChannelItemUpdated[Value]](
        uniqueTag = uniqueTag,
        topic = inputTopic,
        register = itemUpdatedEntity,
        filter = allTrue,
        replayTo = replayTo
      )
      val itemDeleted = KafkaEdge.>.makeConsumerChannel[DChannelItemDeleted[Value]](
        uniqueTag = uniqueTag,
        topic = inputTopic,
        register = itemDeletedEntity,
        filter = allTrue,
        replayTo = replayTo
      )
      val statusChanged = KafkaEdge.>.makeConsumerChannel[DChannelStatusChanged](
        uniqueTag = uniqueTag,
        topic = inputTopic,
        register = statusChangedEntity,
        filter = allTrue,
        replayTo = replayTo
      )
      val effect = for {
        createP <- EdgeCommand.>.makeProducer(
                    uniqueTag = uniqueTag,
                    edgeRef = edgeRef.x,
                    msgFactory = create
                  )
        createdC <- EdgeCommand.>.makeConsumer(uniqueTag = uniqueTag, edgeRef = edgeRef.x, created)
        deleteP  <- EdgeCommand.>.makeProducer(uniqueTag = uniqueTag, edgeRef = edgeRef.x, delete)
        deletedC <- EdgeCommand.>.makeConsumer(uniqueTag = uniqueTag, edgeRef = edgeRef.x, deleted)
        emptySnapshotC <- EdgeCommand.>.makeConsumer(
                           uniqueTag = uniqueTag,
                           edgeRef = edgeRef.x,
                           emptySnapshot
                         )
        snapshotC <- EdgeCommand.>.makeConsumer(
                      uniqueTag = uniqueTag,
                      edgeRef = edgeRef.x,
                      snapshot
                    )
        itemCreatedC <- EdgeCommand.>.makeConsumer(
                         uniqueTag = uniqueTag,
                         edgeRef = edgeRef.x,
                         itemCreated
                       )
        itemUpdatedC <- EdgeCommand.>.makeConsumer(
                         uniqueTag = uniqueTag,
                         edgeRef = edgeRef.x,
                         itemUpdated
                       )
        itemDeletedC <- EdgeCommand.>.makeConsumer(
                         uniqueTag = uniqueTag,
                         edgeRef = edgeRef.x,
                         itemDeleted
                       )
        statusChangedP <- EdgeCommand.>.makeConsumer(
                           uniqueTag = uniqueTag,
                           edgeRef = edgeRef.x,
                           statusChanged
                         )
      } yield DStateConsumer.SubscriptionChannel(
        createChannel = createP,
        createdChannel = createdC,
        deleteChannel = deleteP,
        deletedChannel = deletedC,
        emptySnapshotChannel = emptySnapshotC,
        snapshotChannel = snapshotC,
        itemCreatedChannel = itemCreatedC,
        itemUpdatedChannel = itemUpdatedC,
        itemDeletedChannel = itemDeletedC,
        statusChangedChannel = statusChangedP
      )
      zioRuntime.unsafeRunAsync(effect)(cb)
    }
  }
}
