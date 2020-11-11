package bookingtour.protocols.property.prices.api

import bookingtour.protocols.interlook.source.newTypes._
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.properties.newTypes.{BoardingId, PropertyId, RoomUnitId}
import bookingtour.protocols.property.prices.newTypes.{PriceUnitId, PropertyProviderId, VariantId}
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
final case class PriceUnitOp(
    id: PriceUnitId,
    propertyProvider: PropertyProviderId,
    property: PropertyId,
    propertySync: LookPartyId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    roomUnitId: RoomUnitId,
    typeSync: LookRoomTypeId,
    categorySync: LookRoomCategoryId,
    variant: VariantId,
    accommodationSync: LookAccommodationId,
    boarding: BoardingId,
    boardingSync: LookBoardingId
)

object PriceUnitOp {
  final type Id = PriceUnitId

  implicit final val itemR0: PriceUnitOp => Id = _.id

  implicit final val itemP0: PriceUnitOp => Int = _ => 0

  implicit final val itemP1: PriceUnitOp => LookPartyId = _.propertySync
}
