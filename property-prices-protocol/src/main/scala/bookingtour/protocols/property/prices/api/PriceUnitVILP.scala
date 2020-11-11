package bookingtour.protocols.property.prices.api

import bookingtour.protocols.properties.newTypes.BoardingId
import bookingtour.protocols.property.prices.newTypes.{PriceUnitId, PropertyProviderId, VariantId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PriceUnitVILP(
    id: PriceUnitId,
    propertyProviderId: PropertyProviderId,
    variantId: VariantId,
    boardingId: BoardingId
)

object PriceUnitVILP {
  type Id = PriceUnitId

  implicit val itemR: PriceUnitVILP => Id = _.id

  implicit final val itemP: PriceUnitVILP => PropertyProviderId = _.propertyProviderId
}
