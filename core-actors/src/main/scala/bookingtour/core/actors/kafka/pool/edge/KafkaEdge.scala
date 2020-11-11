package bookingtour.core.actors.kafka.pool.edge

import java.time.Instant
import java.util.UUID

import akka.Done
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props, Stash}
import akka.kafka.ConsumerSettings
import akka.stream.Materializer
import bookingtour.core.kafka.MessageProducer
import bookingtour.protocols.core.actors.kafka.EdgeCommand.{
  EdgeConsumerChannelCreate,
  EdgeConsumerCreateWrapper,
  EdgeProducerChannelCreate,
  EdgeProducerCreateWrapper
}
import bookingtour.protocols.core.actors.kafka.{EdgeHeartbeat, StreamGroup}
import bookingtour.protocols.core.messages.TaggedChannel
import bookingtour.protocols.core.register.RegisterEntity
import cats.syntax.option._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class KafkaEdge private (
    uniqueTag: String,
    consumerSettings: ConsumerSettings[String, String],
    mainTopic: String,
    heartbeatTopic: String,
    topics: Set[StreamGroup],
    heartbeatInterval: Long,
    enableTrace: Boolean
)(
    implicit mat: Materializer,
    runtime: zio.Runtime[zio.ZEnv],
    messageProducer: MessageProducer[Done],
    heartbeatEntity: RegisterEntity.Aux[EdgeHeartbeat]
) extends Actor with Stash with ActorLogging with BasicBehavior with CreateHeartbeatBehavior
    with CreateConsumerBehavior {
  override def preStart(): Unit = {
    super.preStart()
    createHeartbeatBehavior(
      uniqueTag = uniqueTag,
      mainTopic = mainTopic,
      heartbeatTopic = heartbeatTopic,
      consumerSettings = consumerSettings,
      topics = topics,
      heartbeatInterval = heartbeatInterval,
      enableTrace = enableTrace
    )
  }

  def receive: Receive = {
    case _ =>
      stash()
  }
}

object KafkaEdge {
  @newtype final case class EdgeRef(x: ActorRef)

  final case class KafkaEdgeWrapper(pool: EdgeRef)

  final object > {
    def makeConsumer[A](
        uniqueTag: String,
        topic: String,
        register: RegisterEntity.Aux[A],
        filter: A => Boolean,
        dropBefore: Instant = Instant.now(),
        replayTo: ActorRef
    ): ActorRef => EdgeConsumerCreateWrapper[A] =
      mediatorRef =>
        EdgeConsumerCreateWrapper(
          msg = EdgeConsumerChannelCreate(
            id = UUID.randomUUID(),
            uniqueTag = uniqueTag,
            topic = topic,
            dropBefore = dropBefore,
            taggedChannel = None,
            register = register,
            filter = filter,
            replayTo = replayTo
          ),
          mediatorRef = mediatorRef
        )

    def makeConsumerChannel[A](
        uniqueTag: String,
        topic: String,
        register: RegisterEntity.Aux[A],
        filter: A => Boolean,
        dropBefore: Instant = Instant.now(),
        replayTo: ActorRef
    )(implicit taggedChannel: TaggedChannel): ActorRef => EdgeConsumerCreateWrapper[A] =
      mediatorRef =>
        EdgeConsumerCreateWrapper(
          msg = EdgeConsumerChannelCreate(
            id = UUID.randomUUID(),
            uniqueTag = uniqueTag,
            topic = topic,
            dropBefore = dropBefore,
            taggedChannel = taggedChannel.some,
            register = register,
            filter = filter,
            replayTo = replayTo
          ),
          mediatorRef = mediatorRef
        )

    def makeProducerChannel[A](
        uniqueTag: String,
        topic: String,
        register: RegisterEntity.Aux[A],
        replayTo: ActorRef
    )(
        implicit taggedChannel: TaggedChannel
    ): ActorRef => EdgeProducerCreateWrapper[A] =
      mediatorRef =>
        EdgeProducerCreateWrapper(
          msg = EdgeProducerChannelCreate(
            id = UUID.randomUUID(),
            uniqueTag = uniqueTag,
            topic = topic,
            taggedChannel = taggedChannel.some,
            register = register,
            replayTo = replayTo
          ),
          mediatorRef = mediatorRef
        )
  }

  final def make(
      uniqueTag: String,
      consumerSettings: ConsumerSettings[String, String],
      mainTopic: String,
      heartbeatTopic: String,
      topics: Set[StreamGroup],
      heartbeatInterval: Long,
      enableTrace: Boolean
  )(
      implicit system: ActorSystem,
      mat: Materializer,
      runtime: zio.Runtime[zio.ZEnv],
      messageProducer: MessageProducer[Done]
  ): EdgeRef = {
    implicit val a: RegisterEntity.Aux[EdgeHeartbeat] = RegisterEntity[EdgeHeartbeat]()
    system
      .actorOf(
        Props(
          new KafkaEdge(
            uniqueTag = uniqueTag,
            consumerSettings = consumerSettings,
            mainTopic = mainTopic,
            heartbeatTopic = heartbeatTopic,
            topics = topics,
            heartbeatInterval = heartbeatInterval,
            enableTrace = enableTrace
          )
        ),
        uniqueTag
      )
      .coerce[EdgeRef]
  }
}
