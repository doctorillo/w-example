package bookingtour.protocols.properties.agg

import bookingtour.protocols.core.newtypes.quantities.PropertyStar
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.newTypes.{PartyId, ProviderId}
import bookingtour.protocols.parties.values.PartyPREP
import bookingtour.protocols.properties.newTypes.PropertyId
import cats.instances.all._
import bookingtour.protocols.core._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyAGG(
    id: PropertyId,
    property: PartyId,
    propertySync: List[SyncItem],
    supplier: PartyId,
    supplierName: String,
    supplierSync: List[SyncItem],
    provider: ProviderId,
    name: String,
    star: PropertyStar
)

object PropertyAGG {
  type Id = PropertyId

  implicit final val itemR0: PropertyAGG => Id = _.id

  implicit final val itemP0: PropertyAGG => Int = _ => 0

  implicit final class PropertyAGGOps(private val self: PropertyAGG) {
    def propertyLookSync: Option[LookPartyId] =
      self.propertySync.flatMap(PartyPREP.fromSyncItem(_).toList).headOption

    def supplierLookSync: Option[LookPartyId] =
      self.supplierSync.flatMap(PartyPREP.fromSyncItem(_).toList).headOption
  }
}
