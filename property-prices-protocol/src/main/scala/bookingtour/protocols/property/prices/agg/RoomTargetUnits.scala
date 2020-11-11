package bookingtour.protocols.property.prices.agg

import bookingtour.protocols.properties.newTypes.{ PropertyId, RoomUnitId }
import derevo.cats.order
import derevo.circe.{ decoder, encoder }
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.property.prices.newTypes.PropertyProviderId

/**
 * © Alexey Toroshchin 2019.
 */
@derive(encoder, decoder, order)
final case class RoomTargetUnits(
  propertyId: PropertyId,
  propertyProviderId: PropertyProviderId,
  units: List[RoomUnitId]
)
