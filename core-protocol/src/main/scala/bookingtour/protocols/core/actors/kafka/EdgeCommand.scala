package bookingtour.protocols.core.actors.kafka

import java.time.Instant
import java.util.UUID

import akka.actor.ActorRef
import akka.pattern.extended.ask
import akka.util.Timeout
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeChannelError,
  EdgeConsumerChannelCreated,
  EdgeProducerChannelCreated
}
import bookingtour.protocols.core.messages.TaggedChannel
import bookingtour.protocols.core.register.{RegisterEntity, RegisterKey}
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class EdgeCommand[+A](
    val id: UUID,
    val uniqueTag: String,
    val topic: String,
    val replayTo: ActorRef
) extends Product with Serializable

object EdgeCommand {
  final case class EdgeConsumerChannelCreate[A <: Any](
      override val id: UUID,
      override val uniqueTag: String,
      override val topic: String,
      dropBefore: Instant,
      taggedChannel: Option[TaggedChannel],
      register: RegisterEntity.Aux[A],
      filter: A => Boolean,
      override val replayTo: ActorRef
  ) extends EdgeCommand[A](
        id = id,
        uniqueTag = uniqueTag,
        topic = topic,
        replayTo = replayTo
      )

  final case class EdgeConsumerCreateWrapper[A](
      msg: EdgeConsumerChannelCreate[A],
      mediatorRef: ActorRef
  )

  final case class EdgeConsumerChannelDelete(
      override val id: UUID,
      override val uniqueTag: String,
      override val topic: String,
      key: RegisterKey,
      override val replayTo: ActorRef
  ) extends EdgeCommand[Nothing](
        id = id,
        uniqueTag = uniqueTag,
        topic = topic,
        replayTo = replayTo
      )

  final case class EdgeProducerChannelCreate[A <: Any](
      override val id: UUID,
      override val uniqueTag: String,
      override val topic: String,
      taggedChannel: Option[TaggedChannel],
      register: RegisterEntity.Aux[A],
      override val replayTo: ActorRef
  ) extends EdgeCommand[A](
        id = id,
        uniqueTag = uniqueTag,
        topic = topic,
        replayTo = replayTo
      )

  final case class EdgeProducerCreateWrapper[A](
      msg: EdgeProducerChannelCreate[A],
      mediatorRef: ActorRef
  )

  final case class EdgeProducerChannelDelete(
      override val id: UUID,
      override val uniqueTag: String,
      override val topic: String,
      key: RegisterKey,
      override val replayTo: ActorRef
  ) extends EdgeCommand[Nothing](
        id = id,
        uniqueTag = uniqueTag,
        topic = topic,
        replayTo = replayTo
      )

  /*final case class EdgeProducerTopicUnreachable(
    override val id: UUID,
    override val uniqueTag: String,
    override val topic: String,
    override val replayTo: ActorRef
  ) extends EdgeCommand[Nothing](
        id = id,
        uniqueTag = uniqueTag,
        topic = topic,
        replayTo = replayTo
      )*/

  final object > {
    def makeConsumer[A <: Any](
        uniqueTag: String,
        edgeRef: ActorRef,
        msgFactory: ActorRef => EdgeConsumerCreateWrapper[A]
    )(
        implicit timeout: Timeout
    ): ZIO[Any, String, EdgeConsumerChannelCreated] = {
      val tag = s"$uniqueTag. make-edge-consumer"
      for {
        a <- ZIO
              .fromFuture(implicit ec => edgeRef.ask(msgFactory).mapTo[EdgeEvent])
              .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))
        b <- a match {
              case response: EdgeConsumerChannelCreated =>
                ZIO.effectTotal(response)

              case response: EdgeChannelError =>
                ZIO.fail(response.error)

              case response =>
                ZIO.fail(s"$tag. unhandled $response.")
            }
      } yield b
    }

    def makeProducer[A <: Any](
        uniqueTag: String,
        edgeRef: ActorRef,
        msgFactory: ActorRef => EdgeProducerCreateWrapper[A]
    )(
        implicit timeout: Timeout
    ): ZIO[Any, String, EdgeProducerChannelCreated] = {
      val tag = s"$uniqueTag. make-edge-producer"
      for {
        a <- ZIO
              .fromFuture(implicit ec => edgeRef.ask(msgFactory).mapTo[EdgeEvent])
              .catchAll(thr => ZIO.fail(s"$tag. ${thr.getMessage}."))
        b <- a match {
              case msg: EdgeProducerChannelCreated =>
                ZIO.effectTotal(msg)

              case msg: EdgeChannelError =>
                ZIO.fail(msg.error)

              case msg =>
                ZIO.fail(s"$tag. unhandled $msg.")
            }
      } yield b
    }
  }
}
