package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookRoomCategoryId, LookRoomTypeId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomUnitKeyEP(
    property: LookPartyId,
    typeRoom: LookRoomTypeId,
    categoryRoom: LookRoomCategoryId
)

object RoomUnitKeyEP {
  type Id = RoomUnitKeyEP

  implicit final val itemR: RoomUnitKeyEP => Id = x => x

  implicit final val itemP: RoomUnitKeyEP => Int = _ => 0
}
