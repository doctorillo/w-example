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
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyProviderVILP(
    id: PropertyProviderId,
    propertyPartyId: PartyId,
    propertyId: PropertyId,
    propertySyncId: LookPartyId,
    propertyName: String,
    supplierId: PartyId,
    supplierName: String
)

object PropertyProviderVILP {
  type Id = PropertyProviderId

  implicit final val itemR: PropertyProviderVILP => Id = _.id

  implicit final val itemP: PropertyProviderVILP => PartyId = _.supplierId
}
