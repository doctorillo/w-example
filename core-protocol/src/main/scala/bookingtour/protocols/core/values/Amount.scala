package bookingtour.protocols.core.values

import bookingtour.protocols.core._
import bookingtour.protocols.core.types.Sign
import bookingtour.protocols.core.values.enumeration.CurrencyItem
import cats.instances.all._
import cats.kernel.Monoid
import derevo.cats.order
import derevo.derive
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}
import tofu.logging.derivation.loggable
import io.circe.syntax._
import io.circe.derivation._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, loggable)
sealed abstract class Amount(val value: Double, val currency: CurrencyItem) extends Product with Serializable

object Amount {
  def multiply(amount: Amount, m: Long): Amount =
    apply(
      BigDecimal(amount.value * m).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble,
      amount.currency
    )

  @derive(order, loggable)
  final case class Euro(
      override val value: Double,
      override val currency: CurrencyItem = CurrencyItem.Euro
  ) extends Amount(value = value, currency = currency)

  final object Euro {
    implicit val itemMonoid: Monoid[Euro] = new Monoid[Euro] {
      def empty: Euro = Euro(0)

      def combine(x: Euro, y: Euro): Euro = Euro(x.value + y.value)
    }
    implicit val reverseSign: Sign[Euro] = (x: Euro) => Euro(x.value * -1)
  }

  @derive(order, loggable)
  final case class Czk(
      override val value: Double,
      override val currency: CurrencyItem = CurrencyItem.Czk
  ) extends Amount(value = value, currency = currency)

  final object Czk {
    implicit val itemMonoid: Monoid[Czk] = new Monoid[Czk] {
      def empty: Czk = Czk(0)

      def combine(x: Czk, y: Czk): Czk = Czk(x.value + y.value)
    }
    implicit val reverseSign: Sign[Czk] = (x: Czk) => Czk(x.value * -1)
  }

  implicit final val euroEnc: Encoder[Amount.Euro] = deriveEncoder
  implicit final val euroDec: Decoder[Amount.Euro] = deriveDecoder
  implicit final val czkEnc: Encoder[Amount.Czk]   = deriveEncoder
  implicit final val czkDec: Decoder[Amount.Czk]   = deriveDecoder

  implicit final val amountEnc: Encoder[Amount] = {
    case p: Euro =>
      p.asJson(euroEnc)
    case p: Czk =>
      p.asJson(czkEnc)
  }
  implicit final val amountDec: Decoder[Amount] = (c: HCursor) => {
    val cv = Decoder[CurrencyItem].prepare(_.downField("currency"))(c)
    cv match {
      case Left(thr: DecodingFailure) =>
        Left(thr)

      case Right(item) =>
        item match {
          case CurrencyItem.Euro =>
            euroDec(c)

          case CurrencyItem.Czk =>
            czkDec(c)
        }
    }
  }

  def apply(amount: Double, currency: CurrencyItem): Amount = currency match {
    case CurrencyItem.Euro =>
      Amount.Euro(amount)

    case CurrencyItem.Czk =>
      Amount.Czk(amount)
  }
}
