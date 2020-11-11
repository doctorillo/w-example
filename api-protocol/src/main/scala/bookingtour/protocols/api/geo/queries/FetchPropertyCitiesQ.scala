package bookingtour.protocols.api.geo.queries

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.Order
import cats.syntax.order._
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class FetchPropertyCitiesQ(lang: LangItem, editorPartyId: UUID)

object FetchPropertyCitiesQ {
  implicit final val fetchPropertyCitiesQEnc: Encoder[FetchPropertyCitiesQ] = deriveEncoder
  implicit final val fetchPropertyCitiesQDec: Decoder[FetchPropertyCitiesQ] = deriveDecoder

  implicit final val fetchPropertyCitiesQO: Order[FetchPropertyCitiesQ] =
    (x: FetchPropertyCitiesQ, y: FetchPropertyCitiesQ) =>
      CompareOps.compareFn(
        x.lang.compare(y.lang),
        x.editorPartyId.compareTo(y.editorPartyId)
      )
}
