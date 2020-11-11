package bookingtour.core.actors.kafka.pool.producer

import java.util.UUID

import akka.Done
import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props, Timers}
import bookingtour.core.kafka.MessageProducer
import bookingtour.protocols.core.actors.kafka.EdgeCommand.{
  EdgeProducerChannelCreate,
  EdgeProducerChannelDelete,
  EdgeProducerCreateWrapper
}
import bookingtour.protocols.core.actors.kafka.EdgeEvent.{
  EdgeProducerChannelCreated,
  EdgeProducerChannelDeleted,
  EdgeTopicUnreachableReceived
}
import bookingtour.protocols.core.actors.kafka.EdgeProducerCommand.{EdgePublish, EdgePublishWithEnvelope}
import bookingtour.protocols.core.messages.EnvelopeRoute.Bridge
import bookingtour.protocols.core.messages.MessageEnvelope.EnvelopeChannel
import bookingtour.protocols.core.messages.{PostOffice, PostStamp}
import bookingtour.protocols.core.register.RegisterEntity
import cats.data.NonEmptyList
import cats.instances.uuid._
import cats.syntax.order._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
final class EdgeProducer[Value] private (
    uniqueTag: String,
    topic: String,
    enableTrace: Boolean
)(
    implicit producer: MessageProducer[Done],
    runtime: zio.Runtime[zio.ZEnv],
    entityValue: RegisterEntity.Aux[Value]
) extends Actor with Timers with ActorLogging {
  private val postOffice: PostOffice = PostOffice(name = uniqueTag)
  (x: ActorRef, y: ActorRef) => x.compareTo(y)

  private def behaviors(producers: List[EdgeProducerChannelCreate[_]]): Receive = {
    case wrapper: EdgeProducerCreateWrapper[_] =>
      wrapper.mediatorRef ! EdgeProducerChannelCreated(id = wrapper.msg.id, replayTo = self)
      if (!producers.exists(_.id === wrapper.msg.id)) {
        context.become(behaviors(producers :+ wrapper.msg))
      }

    case msg: EdgeProducerChannelDelete =>
      if (producers.exists(_.id === msg.id)) {
        context.become(behaviors(producers.filterNot(_.id === msg.id)))
      }
      msg.replayTo ! EdgeProducerChannelDeleted(id = msg.id, replayTo = self)

    case msg: EdgePublish[_] =>
      val tag = s"$uniqueTag. edge-publish received"
      producers.find(_.id === msg.id) match {
        case None =>
          log.error(s"$tag. producer subscription not found.")

        case Some(subscription) =>
          val effect = for {
            a <- ZIO.effectTotal(
                  EnvelopeChannel(
                    id = UUID.randomUUID(),
                    route = Bridge(output = subscription.topic, input = topic),
                    channel = msg.channel,
                    bodyKey = entityValue.key,
                    stamps = NonEmptyList.one(PostStamp(office = postOffice)),
                    expiredAt = msg.expiredAt
                  )
                )
            b <- ZIO
                  .effect(msg.msg.asInstanceOf[Value])
                  .catchAll(thr => ZIO.fail(s"$uniqueTag. ${thr.getMessage}."))
            c <- entityValue.encode(b)
            d <- producer.publishMessage(a, c)
          } yield d
          runtime.unsafeRunAsync(effect) {
            case zio.Exit.Failure(cause) =>
              cause.failures.foreach(log.error)

            case zio.Exit.Success(_) =>
              if (enableTrace) {
                log.info(s"$uniqueTag. published.")
              }
          }
      }

    case msg: EdgePublishWithEnvelope[_] =>
      producers.find(_.id === msg.id) match {
        case None =>
          log.error(s"$uniqueTag. producer subscription not found.")

        case Some(_) =>
          val effect = for {
            a <- ZIO.effectTotal(msg.envelope)
            b <- ZIO
                  .effect(msg.msg.asInstanceOf[Value])
                  .catchAll(thr => ZIO.fail(s"$uniqueTag. ${thr.getMessage}."))
            c <- entityValue.encode(b)
            d <- producer.publishMessage(a, c)
          } yield d
          runtime.unsafeRunAsync(effect) {
            case zio.Exit.Failure(cause) =>
              cause.failures.foreach(log.error)

            case zio.Exit.Success(_) =>
              if (enableTrace) {
                log.info(s"$uniqueTag. published.")
              }
          }
      }

    case msg: EdgeTopicUnreachableReceived =>
      if (enableTrace) {
        log.info(s"$uniqueTag. edge-topic-unreachable-received received.")
      }
      producers.groupBy(_.replayTo).map(_._1 ! msg)

    case msg =>
      log.error(s"$uniqueTag. unhandled $msg.")
      context.stop(self)
  }

  def receive: Receive = behaviors(List.empty)
}

object EdgeProducer {
  @newtype final case class EdgeProducerRef[Value](x: ActorRef)

  final def make[Value: RegisterEntity.Aux](
      topic: String,
      enableTrace: Boolean
  )(
      implicit ctx: ActorContext,
      producer: MessageProducer[Done],
      runtime: zio.Runtime[zio.ZEnv]
  ): EdgeProducerRef[Value] = {
    val entity: RegisterEntity.Aux[Value] = implicitly
    val uniqueTag                         = s"producer:$topic:${entity.key.typeTag}:${entity.key.version}"
    ctx
      .actorOf(
        Props(
          new EdgeProducer(
            uniqueTag = uniqueTag,
            topic = topic,
            enableTrace = enableTrace
          )
        ),
        uniqueTag
      )
      .coerce[EdgeProducerRef[Value]]
  }
}
