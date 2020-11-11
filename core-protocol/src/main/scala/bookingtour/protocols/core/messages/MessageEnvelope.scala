package bookingtour.protocols.core.messages

import java.time.{Duration, Instant}
import java.util.UUID

import bookingtour.protocols.core.messages.EnvelopeRoute.{Bridge, ConsumerOnly, PublisherOnly}
import bookingtour.protocols.core.register.RegisterKey
import bookingtour.protocols.core.types.CompareOps
import cats.Order
import cats.data.NonEmptyList
import cats.syntax.option._
import cats.syntax.order._
import io.circe.derivation._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class MessageEnvelope(
    val id: UUID,
    val route: EnvelopeRoute,
    val bodyKey: RegisterKey,
    val stamps: NonEmptyList[PostStamp],
    val expiredAt: Instant
) extends Product with Serializable

object MessageEnvelope {
  final case class EnvelopeSimple(
      override val id: UUID,
      override val route: EnvelopeRoute,
      override val bodyKey: RegisterKey,
      override val stamps: NonEmptyList[PostStamp],
      override val expiredAt: Instant
  ) extends MessageEnvelope(id, route, bodyKey, stamps, expiredAt)

  object EnvelopeSimple {
    implicit final val circeEnc: Encoder.AsObject[EnvelopeSimple] =
      deriveEncoder
    implicit final val circeDec: Decoder[EnvelopeSimple] =
      deriveDecoder
    implicit final val catsO: Order[EnvelopeSimple] =
      (x: EnvelopeSimple, y: EnvelopeSimple) =>
        CompareOps.compareFn(
          x.id.compareTo(y.id),
          x.route.compare(y.route),
          x.bodyKey.compare(y.bodyKey),
          x.stamps.compare(y.stamps),
          x.expiredAt.compareTo(y.expiredAt)
        )
  }

  final case class EnvelopeChannel(
      override val id: UUID,
      override val route: EnvelopeRoute,
      override val bodyKey: RegisterKey,
      channel: TaggedChannel,
      override val stamps: NonEmptyList[PostStamp],
      override val expiredAt: Instant
  ) extends MessageEnvelope(id, route, bodyKey, stamps, expiredAt)

  object EnvelopeChannel {
    implicit final val circe2Enc: Encoder.AsObject[EnvelopeChannel] =
      deriveEncoder
    implicit final val circe2Dec: Decoder[EnvelopeChannel] =
      deriveDecoder
    implicit final val cats2O: Order[EnvelopeChannel] =
      (x: EnvelopeChannel, y: EnvelopeChannel) =>
        CompareOps.compareFn(
          x.id.compareTo(y.id),
          x.route.compare(y.route),
          x.bodyKey.compare(y.bodyKey),
          x.channel.compare(y.channel),
          x.stamps.compare(y.stamps),
          x.expiredAt.compareTo(y.expiredAt)
        )
  }

  implicit final val messageEnvelopeEnc: Encoder.AsObject[MessageEnvelope] =
    deriveEncoder

  implicit final val messageEnvelopeDec: Decoder[MessageEnvelope] =
    deriveDecoder

  implicit final class MessageEnvelopeOps(private val self: MessageEnvelope) extends AnyVal {
    def recipient: Option[String] = self.route match {
      case Bridge(output, _, _) =>
        output.some

      case PublisherOnly(output, _) =>
        output.some

      case ConsumerOnly(_, _) =>
        none[String]
    }

    def sender: Option[String] = self.route match {
      case Bridge(_, input, _) =>
        input.some

      case PublisherOnly(_, _) =>
        none[String]

      case ConsumerOnly(input, _) =>
        input.some
    }

    def live(now: Instant = Instant.now()): Boolean =
      self.expiredAt.isAfter(now)

    def created: Instant = self.stamps.head.stamp

    def postStamp(
        stamp: Instant = Instant.now()
    )(implicit office: PostOffice): MessageEnvelope = {
      val s = PostStamp(
        office = office,
        stamp = stamp
      )
      self match {
        case e: EnvelopeSimple =>
          e.copy(
            stamps = self.stamps :+ s
          )
        case e: EnvelopeChannel =>
          e.copy(
            stamps = self.stamps :+ s
          )
      }
    }

    def stamping(implicit o: PostOffice): MessageEnvelope = self match {
      case e: EnvelopeSimple =>
        e.copy(
          stamps = self.stamps :+ PostStamp(
            office = o,
            stamp = Instant.now()
          )
        )
      case e: EnvelopeChannel =>
        e.copy(
          stamps = self.stamps :+ PostStamp(
            office = o,
            stamp = Instant.now()
          )
        )
    }

    def timeTravel: Long =
      Duration.between(created, Instant.now()).toMillis
  }
}
