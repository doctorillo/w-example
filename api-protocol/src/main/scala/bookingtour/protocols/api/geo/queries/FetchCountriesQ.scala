package bookingtour.protocols.api.geo.queries

import bookingtour.protocols.core.values.enumeration.LangItem
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class FetchCountriesQ(lang: LangItem)

object FetchCountriesQ {
  implicit final val fetchCountriesQEnc: Encoder[FetchCountriesQ] = deriveEncoder
  implicit final val fetchCountriesQDec: Decoder[FetchCountriesQ] = deriveDecoder

  implicit final val fetchCountriesQO: Order[FetchCountriesQ] =
    (x: FetchCountriesQ, y: FetchCountriesQ) => x.lang.value.compareTo(y.lang.value)
}
