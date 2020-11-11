package bookingtour.protocols.core.messages

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import cats.syntax.order._
import io.circe.derivation._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class EnvelopeRoute(val tag: Int) extends Product with Serializable {
  def prettyPrint: String
}

object EnvelopeRoute {
  final case class Bridge(output: String, input: String, override val tag: Int = 0) extends EnvelopeRoute(tag) {
    def prettyPrint: String = s"bridge: $input=>$output."

    def revert(): Bridge = Bridge(output = input, input = output)
  }
  object Bridge {
    implicit final val bridgeEnc: Encoder[Bridge] = deriveEncoder
    implicit final val bridgeDec: Decoder[Bridge] = deriveDecoder
    implicit final val bridgeO: Order[Bridge] = (x: Bridge, y: Bridge) =>
      CompareOps.compareFn(x.output.compareTo(y.output), x.input.compareTo(y.input))
  }

  final case class PublisherOnly(output: String, override val tag: Int = 1) extends EnvelopeRoute(tag) {
    def prettyPrint: String = s"publisher-only: =>$output."
  }
  object PublisherOnly {
    implicit final val publisherOnlyEnc: Encoder[PublisherOnly] = deriveEncoder
    implicit final val publisherOnlyDec: Decoder[PublisherOnly] = deriveDecoder
    implicit final val publisherOnlyO: Order[PublisherOnly] =
      (x: PublisherOnly, y: PublisherOnly) => x.output.compareTo(y.output)
  }

  final case class ConsumerOnly(input: String, override val tag: Int = 2) extends EnvelopeRoute(tag) {
    def prettyPrint: String = s"consumer-only: $input=>."
  }
  object ConsumerOnly {
    implicit final val consumerOnlyEnc: Encoder[ConsumerOnly] = deriveEncoder
    implicit final val consumerOnlyDec: Decoder[ConsumerOnly] = deriveDecoder
    implicit final val consumerOnlyO: Order[ConsumerOnly] = (x: ConsumerOnly, y: ConsumerOnly) =>
      x.input.compareTo(y.input)
  }

  implicit final val envelopeRouteEnc: Encoder[EnvelopeRoute] = Encoder.instance {
    case msg: Bridge =>
      msg.asJson

    case msg: PublisherOnly =>
      msg.asJson

    case msg: ConsumerOnly =>
      msg.asJson
  }

  implicit final val envelopeRouteDec: Decoder[EnvelopeRoute] = Decoder.instance { cursor =>
    cursor.downField("tag").as[Int] match {
      case Left(thr) =>
        Left(thr)

      case Right(tag) =>
        tag match {
          case 0 =>
            cursor.as[Bridge]

          case 1 =>
            cursor.as[PublisherOnly]

          case 2 =>
            cursor.as[ConsumerOnly]
        }
    }
  }

  implicit final val envelopeRouteO: Order[EnvelopeRoute] = (x: EnvelopeRoute, y: EnvelopeRoute) =>
    (x, y) match {
      case (xx: Bridge, yy: Bridge) =>
        xx.compare(yy)

      case (xx: PublisherOnly, yy: PublisherOnly) =>
        xx.compare(yy)

      case (xx: ConsumerOnly, yy: ConsumerOnly) =>
        xx.compare(yy)

      case (xx, yy) =>
        xx.tag.compareTo(yy.tag)
    }
}
