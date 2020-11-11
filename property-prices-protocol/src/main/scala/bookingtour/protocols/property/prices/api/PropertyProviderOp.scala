package bookingtour.protocols.property.prices.api

import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.properties.newTypes.PropertyId
import bookingtour.protocols.property.prices.newTypes.PropertyProviderId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PropertyProviderOp(
    id: PropertyProviderId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    property: PropertyId,
    propertyParty: PartyId,
    propertySync: LookPartyId
)

object PropertyProviderOp {
  final type Id = PropertyProviderId

  implicit final val itemR0: PropertyProviderOp => Id = _.id

  implicit final val itemP0: PropertyProviderOp => Int = _ => 0
}
