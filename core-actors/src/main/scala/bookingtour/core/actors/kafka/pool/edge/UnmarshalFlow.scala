package bookingtour.core.actors.kafka.pool.edge

import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.event.LoggingAdapter
import akka.kafka.ConsumerMessage
import akka.stream.scaladsl.Flow
import bookingtour.core.actors.kafka.pool.HEADER_ENVELOPE
import bookingtour.core.actors.kafka.pool.worker.KafkaConsumerWorker.{EdgeConsumerRef, EnvelopedMessage}
import bookingtour.protocols.core.messages.MessageEnvelope
import scala.jdk.CollectionConverters._

/**
  * © Alexey Toroshchin 2019.
  */
object UnmarshalFlow {
  final def make(
      topic: String,
      worker: EdgeConsumerRef,
      log: LoggingAdapter,
      enableTrace: Boolean
  ): Flow[
    ConsumerMessage.CommittableMessage[String, String],
    ConsumerMessage.CommittableOffset,
    NotUsed
  ] = Flow.fromFunction { msg: ConsumerMessage.CommittableMessage[String, String] =>
    if (enableTrace) {
      log.info(s"$topic:flow. committable-message received.")
    }
    msg.record.headers().headers(HEADER_ENVELOPE).iterator().asScala.toList.foreach { header =>
      val json = new String(header.value(), StandardCharsets.UTF_8)
      io.circe.parser.decode[MessageEnvelope](json) match {
        case Left(err) =>
          log.error(s"$topic. msg: $json. flow. ${err.getMessage}.")

        case Right(envelope) =>
          worker.x ! EnvelopedMessage(envelope, msg.record.value())
      }
    }
    msg.committableOffset
  }
}
