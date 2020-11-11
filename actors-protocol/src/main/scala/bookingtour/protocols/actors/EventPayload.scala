package bookingtour.protocols.actors

import cats.Order
import cats.data.NonEmptyChain
import cats.syntax.order._
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor}

/**
  * © Alexey Toroshchin 2019.
  */
sealed trait EventPayload[+A] {
  val category: PayloadCategory
}

object EventPayload {
  @derive(encoder, decoder)
  final case class Flushed(override val category: PayloadCategory = PayloadCategory.Flush) extends EventPayload[Nothing]

  /*final object Flushed {
    implicit val flushedEnc: Encoder[Flushed] = deriveEncoder
    implicit val flushedDec: Decoder[Flushed] = deriveDecoder
  }*/

  @derive(encoder, decoder)
  final case class Data[A](xs: NonEmptyChain[A], override val category: PayloadCategory) extends EventPayload[A]

  /*final object Data {
    implicit def dataEnc[A: Encoder]: Encoder[Data[A]] = deriveEncoder

    implicit def dataDec[A: Decoder]: Decoder[Data[A]] = deriveDecoder
  }*/
  implicit final def eventPayloadDataEnc[A: Encoder]: Encoder[EventPayload[A]] = Encoder.instance {
    case msg: Flushed =>
      msg.asJson

    case msg: Data[A] =>
      msg.asJson
  }

  implicit final def eventPayloadDataDec[A: Decoder]: Decoder[EventPayload[A]] = Decoder.instance { cur: HCursor =>
    cur.downField("xs").as[NonEmptyChain[A]] match {
      case Left(_) =>
        cur.as[Flushed]

      case Right(_) =>
        cur.as[Data[A]]
    }
  }

  implicit final def eventPayloadO[A]: Order[EventPayload[A]] =
    (x: EventPayload[A], y: EventPayload[A]) => x.category.compare(y.category)
}
