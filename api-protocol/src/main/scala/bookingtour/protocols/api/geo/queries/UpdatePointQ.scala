package bookingtour.protocols.api.geo.queries

import java.util.UUID

import bookingtour.protocols.core.types.CompareOps
import cats.Order
import io.circe.derivation.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
  * © Alexey Toroshchin 2019.
  */
final case class UpdatePointQ(
    id: UUID,
    dataId: UUID,
    lng: Double,
    lat: Double
)

object UpdatePointQ {
  implicit final val updatePointQEnc: Encoder[UpdatePointQ] = deriveEncoder
  implicit final val updatePointQDec: Decoder[UpdatePointQ] = deriveDecoder

  implicit final val updatePointQO: Order[UpdatePointQ] =
    (x: UpdatePointQ, y: UpdatePointQ) =>
      CompareOps.compareFn(
        x.id.compareTo(y.id),
        x.dataId.compareTo(y.dataId),
        x.lng.compareTo(y.lng),
        x.lat.compareTo(y.lat)
      )
}
