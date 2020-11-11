package bookingtour.core.actors.kafka.pool.worker

import java.time.Instant

import scala.collection.immutable.Map

import akka.actor.{Actor, ActorContext, ActorLogging, ActorRef, Props}
import bookingtour.core.actors.kafka.pool.worker.WorkerProcessor.EdgeWorkerRef
import bookingtour.protocols.core.actors.kafka.EdgeCommand.{EdgeConsumerChannelDelete, EdgeConsumerCreateWrapper}
import bookingtour.protocols.core.messages.MessageEnvelope
import bookingtour.protocols.core.register.{RegisterEntity, RegisterKey}
import cats.syntax.order._
import io.estatico.newtype.macros.newtype
import io.estatico.newtype.ops._

/**
  * © Alexey Toroshchin 2019.
  */
final class KafkaConsumerWorker private (
    uniqueTag: String,
    topic: String,
    enableTrace: Boolean
)(
    implicit zioRuntime: zio.Runtime[zio.ZEnv]
) extends Actor with ActorLogging {
  import KafkaConsumerWorker._

  private val dropBefore: Instant = Instant.now().minusSeconds(60L)

  private def behaviors(
      consumers: Map[RegisterKey, EdgeWorkerRef]
  ): Receive = {
    case wrapper: EdgeConsumerCreateWrapper[_] =>
      consumers.find(_._1 === wrapper.msg.register.key) match {
        case Some((_, processor)) =>
          processor.x.forward(wrapper)

        case None =>
          context.watch(wrapper.msg.replayTo)
          implicit val decoder: RegisterEntity.Aux[wrapper.msg.register.Result] =
            wrapper.msg.register
          val processor = WorkerProcessor.make[wrapper.msg.register.Result](
            uniqueTag = s"$uniqueTag:${wrapper.msg.register.key.typeTag}:${wrapper.msg.register.key.version}:processor",
            enableTrace = enableTrace
          )
          context.become(
            behaviors(consumers + (wrapper.msg.register.key -> processor))
          )
          processor.x.forward(wrapper)
      }

    case msg: EdgeConsumerChannelDelete =>
      val tag = s"$uniqueTag. $topic. delete-kafka-consumer-channel"
      consumers.find(_._1 === msg.key) match {
        case Some((_, processor)) =>
          processor.x.forward(msg)

        case None =>
          log.error(s"$tag. unhandled message with id: ${msg.key}.")
      }

    case msg @ EnvelopedMessage(envelope, _) if envelope.expiredAt.isAfter(dropBefore) =>
      consumers.find(_._1 === envelope.bodyKey) match {
        case Some((_, processor)) =>
          processor.x.forward(msg)

        case None =>
      }

    case msg =>
      log.info(s"$uniqueTag. unhandled $msg.")
  }

  def receive: Receive = behaviors(Map.empty)
}

object KafkaConsumerWorker {
  final case class EnvelopedMessage(envelope: MessageEnvelope, body: String)

  @newtype final case class EdgeConsumerRef(x: ActorRef)

  final def make(
      uniqueTag: String,
      topic: String,
      enableTrace: Boolean
  )(
      implicit ctx: ActorContext,
      runtime: zio.Runtime[zio.ZEnv]
  ): EdgeConsumerRef =
    ctx
      .actorOf(
        Props(
          new KafkaConsumerWorker(
            uniqueTag = uniqueTag,
            topic = topic,
            enableTrace = enableTrace
          )
        )
      )
      .coerce[EdgeConsumerRef]
}
