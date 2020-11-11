package bookingtour.protocols.api.geo.queries

import bookingtour.protocols.core.values.enumeration.LangItem
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class FetchCitiesQ(lang: LangItem)

object FetchCitiesQ {
  implicit final val fetchCitiesQEnc: Encoder[FetchCitiesQ] = deriveEncoder
  implicit final val fetchCitiesQDec: Decoder[FetchCitiesQ] = deriveDecoder

  implicit final val fetchCitiesQO: Order[FetchCitiesQ] = (x: FetchCitiesQ, y: FetchCitiesQ) =>
    x.lang.value.compareTo(y.lang.value)
}
