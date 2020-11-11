package bookingtour.protocols.parties.api.queries

import bookingtour.protocols.core.values.enumeration.{ContextItem, LangItem}
import cats.Order
import cats.instances.int._
import cats.syntax.order._
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class FetchPointsQ(val ctx: ContextItem, val lang: LangItem) extends Product with Serializable

object FetchPointsQ {
  final case class Property(override val lang: LangItem)
      extends FetchPointsQ(ctx = ContextItem.Accommodation, lang = lang)

  object Property {
    implicit final val fetchPropertyPointsQEnc: Encoder[Property] = deriveEncoder
    implicit final val fetchPropertyPointsQDec: Decoder[Property] = deriveDecoder
    implicit final val fetchPointsPropertyQO: Order[Property] =
      (x: Property, y: Property) => x.lang.value.compareTo(y.lang.value)
  }

  final case class Transfer(override val lang: LangItem) extends FetchPointsQ(ctx = ContextItem.Transfer, lang = lang)

  object Transfer {
    implicit final val fetchTransferPointsQEnc: Encoder[Transfer] = deriveEncoder
    implicit final val fetchTransferPointsQDec: Decoder[Transfer] = deriveDecoder
    implicit final val fetchPointsTransferQO: Order[Transfer] =
      (x: Transfer, y: Transfer) => x.lang.value.compareTo(y.lang.value)
  }

  final case class Excursion(override val lang: LangItem) extends FetchPointsQ(ctx = ContextItem.Excursion, lang = lang)

  object Excursion {
    implicit final val fetchExcursionPointsQEnc: Encoder[Excursion] = deriveEncoder
    implicit final val fetchExcursionPointsQDec: Decoder[Excursion] = deriveDecoder
    implicit final val fetchPointsExcursionQO: Order[Excursion] =
      (x: Excursion, y: Excursion) => x.lang.value.compareTo(y.lang.value)
  }

  implicit final val fetchPointsQEnc: Encoder[FetchPointsQ] = Encoder.instance {
    case a: Property =>
      a.asJson
    case b: Transfer =>
      b.asJson
    case c: Excursion =>
      c.asJson
  }

  implicit final val fetchPointsQDec: Decoder[FetchPointsQ] = (c: HCursor) => {
    val ctx = for {
      a <- c.downField("ctx").as[ContextItem]
    } yield a
    ctx match {
      case Right(ContextItem.Accommodation) =>
        c.as[Property]

      case Right(ContextItem.Transfer) =>
        c.as[Transfer]

      case Right(ContextItem.Excursion) =>
        c.as[Excursion]

      case _ =>
        Left(DecodingFailure.fromThrowable(new Throwable("undefined context"), List.empty))
    }
  }

  implicit final val fetchPointsQO: Order[FetchPointsQ] =
    (x: FetchPointsQ, y: FetchPointsQ) => {
      val ctx  = x.ctx.value.compareTo(y.ctx.value)
      val lang = x.lang.value.compareTo(y.lang.value)
      if (ctx =!= 0) {
        ctx
      } else {
        lang
      }
    }
}
