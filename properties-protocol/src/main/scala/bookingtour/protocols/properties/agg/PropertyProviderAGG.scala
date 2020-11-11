package bookingtour.protocols.properties.agg

import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.newTypes._
import bookingtour.protocols.properties.newTypes.PropertyId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PropertyProviderAGG(
    id: PropertyId,
    property: PartyId,
    propertySync: List[SyncItem],
    supplier: PartyId,
    supplierSync: List[SyncItem]
)
object PropertyProviderAGG {
  type Id = PropertyId

  implicit final val itemR0: PropertyProviderAGG => Id = _.id

  implicit final val itemP0: PropertyProviderAGG => Int = _ => 0
}
