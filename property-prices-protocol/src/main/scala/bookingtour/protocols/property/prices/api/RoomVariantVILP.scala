package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.{PaxOnExtraBed, PaxOnMain}
import bookingtour.protocols.properties.newTypes.RoomUnitId
import bookingtour.protocols.property.prices.newTypes.{PropertyProviderId, VariantId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomVariantVILP(
    id: VariantId,
    propertyProviderId: PropertyProviderId,
    roomUnitId: RoomUnitId,
    onMain: PaxOnMain,
    onExb: PaxOnExtraBed,
    guests: List[AccommodationVILP]
)

object RoomVariantVILP {
  type Id = VariantId

  implicit final val itemR: RoomVariantVILP => Id = _.id

  implicit final val itemP: RoomVariantVILP => PropertyProviderId = _.propertyProviderId
}
