package bookingtour.protocols.api.geo.queries

import bookingtour.protocols.core.values.enumeration.LangItem
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class FetchRegionsQ(lang: LangItem)

object FetchRegionsQ {
  implicit final val fetchRegionsQEnc: Encoder[FetchRegionsQ] = deriveEncoder
  implicit final val fetchRegionsQDec: Decoder[FetchRegionsQ] = deriveDecoder

  implicit final val fetchRegionsQO: Order[FetchRegionsQ] =
    (x: FetchRegionsQ, y: FetchRegionsQ) => x.lang.value.compareTo(y.lang.value)
}
