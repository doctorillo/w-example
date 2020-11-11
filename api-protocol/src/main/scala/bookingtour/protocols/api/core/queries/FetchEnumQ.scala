package bookingtour.protocols.api.core.queries

import bookingtour.protocols.core.values.enumeration.LangItem
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class FetchEnumQ(lang: LangItem)

object FetchEnumQ {
  implicit final val fetchEnumQEnc: Encoder[FetchEnumQ] = deriveEncoder
  implicit final val fetchEnumQDec: Decoder[FetchEnumQ] = deriveDecoder

  implicit final val fetchEnumQO: Order[FetchEnumQ] =
    (x: FetchEnumQ, y: FetchEnumQ) => x.lang.value.compareTo(y.lang.value)
}
