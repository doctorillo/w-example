package bookingtour.core.actors.kafka.pool.producer

import java.nio.charset.StandardCharsets

import scala.jdk.CollectionConverters._
import scala.concurrent.Future

import akka.Done
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.{Producer => KProducer}
import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}
import bookingtour.core.actors.kafka.pool._
import bookingtour.core.kafka.MessageProducer
import bookingtour.protocols.core.messages.EnvelopeRoute.{Bridge, PublisherOnly}
import bookingtour.protocols.core.messages.MessageEnvelope
import bookingtour.protocols.core.register.RegisterEntity
import io.circe.Encoder
import org.apache.kafka.clients.producer.{Producer, ProducerRecord}
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import zio.ZIO

/**
  * © Alexey Toroshchin 2019.
  */
final class AlpakkaKafkaProducer private (
    settings: ProducerSettings[String, String],
    producer: Producer[String, String],
    enableTrace: Boolean
)(implicit m: Materializer, e: RegisterEntity.Aux[MessageEnvelope])
    extends MessageProducer[Done] {
  private[this] val instance: Sink[ProducerRecord[String, String], Future[Done]] =
    KProducer.plainSink(settings, producer)

  def publishMessage(
      envelope: MessageEnvelope,
      message: String
  ): ZIO[Any, String, Done] =
    for {
      topic <- envelope.route match {
                case Bridge(output, _, _) =>
                  ZIO.effectTotal(output)

                case PublisherOnly(output, _) =>
                  ZIO.effectTotal(output)

                case msg =>
                  ZIO.fail(s"publish-message. route: $msg.")
              }
      header <- e.encode(envelope)
      headers <- ZIO.effectTotal(
                  Seq(
                    new RecordHeader(HEADER_ENVELOPE, header.getBytes(StandardCharsets.UTF_8))
                      .asInstanceOf[Header]
                  ).asJavaCollection
                )
      record <- ZIO
                 .effect(
                   new ProducerRecord[String, String](
                     topic,
                     null,
                     null,
                     message,
                     headers
                   )
                 )
                 .catchAll(thr => ZIO.fail(s"${envelope.recipient}. step: create-record. ${thr.getMessage}"))
      done <- ZIO
               .fromFuture(implicit ec => Source.single(record).runWith(instance))
               .catchAll(thr => ZIO.fail(s"${envelope.recipient}. step: publish. ${thr.getMessage}"))
    } yield done

  def publishEffect(
      envelope: MessageEnvelope,
      message: ZIO[Any, String, String]
  ): ZIO[Any, String, Done] =
    for {
      body <- message
      done <- publishMessage(envelope, body)
    } yield done
}

object AlpakkaKafkaProducer {
  import bookingtour.core.actors.kafka.pool.makeProducer

  final def make(
      settings: ProducerSettings[String, String],
      enableTrace: Boolean
  )(implicit m: Materializer, e: Encoder[MessageEnvelope]): MessageProducer[Done] = {
    implicit val producer: Producer[String, String] = makeProducer(settings)
    new AlpakkaKafkaProducer(settings, producer, enableTrace)
  }
}
