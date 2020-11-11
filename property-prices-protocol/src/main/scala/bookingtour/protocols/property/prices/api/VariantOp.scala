package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.{PaxOnExtraBed, PaxOnMain}
import bookingtour.protocols.interlook.source.newTypes._
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.properties.newTypes.{PropertyId, RoomUnitId}
import bookingtour.protocols.property.prices.newTypes.{AccommodationId, PropertyProviderId, VariantId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import tofu.logging.derivation.{loggable}

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order, loggable)
final case class VariantOp(
    id: VariantId,
    propertyProvider: PropertyProviderId,
    property: PropertyId,
    propertySync: LookPartyId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    accommodation: AccommodationOp,
    roomUnit: RoomUnitId,
    typeSync: LookRoomTypeId,
    categorySync: LookRoomCategoryId
)

object VariantOp {
  final type Id = VariantId

  implicit final val itemR0: VariantOp => Id = _.id

  implicit final val itemP0: VariantOp => Int = _ => 0

  implicit final class VariantOpOps(private val self: VariantOp) {
    def accommodationId: AccommodationId = self.accommodation.id

    def accommodationSync: LookAccommodationId = self.accommodation.accommodationSync

    def onMain: PaxOnMain = self.accommodation.onMain

    def onExb: PaxOnExtraBed = self.accommodation.onExb
  }
}
