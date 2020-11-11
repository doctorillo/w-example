package bookingtour.protocols.interlook.source.properties

import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyRoomUnitEP(
    id: RoomUnitKeyEP,
    roomType: RoomTypeEP,
    roomCategory: RoomCategoryEP
)

object PropertyRoomUnitEP {
  type Id = RoomUnitKeyEP

  implicit final val itemR: PropertyRoomUnitEP => Id = _.id

  implicit final val itemP: PropertyRoomUnitEP => Int = _ => 0
}
