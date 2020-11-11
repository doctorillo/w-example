package bookingtour.protocols.interlook.source.prices

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{
  LookAccommodationId,
  LookBoardingId,
  LookPartyId,
  LookRoomCategoryId,
  LookRoomTypeId
}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CostBaseEP(
    property: LookPartyId,
    supplier: LookPartyId,
    typeRoom: LookRoomTypeId,
    categoryRoom: LookRoomCategoryId,
    accommodation: LookAccommodationId,
    boarding: LookBoardingId
)

object CostBaseEP {
  type Id = CostBaseEP

  implicit final val itemR: CostBaseEP => Id = x => x

  implicit final val itemP: CostBaseEP => LookPartyId = _.property
}
