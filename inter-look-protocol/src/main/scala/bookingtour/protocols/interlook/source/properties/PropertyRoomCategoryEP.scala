package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyRoomCategoryEP(id: RoomCategoryKeyEP, roomCategory: RoomCategoryEP)

object PropertyRoomCategoryEP {
  type Id = RoomCategoryKeyEP

  implicit final val itemR: PropertyRoomCategoryEP => Id = _.id

  implicit final val itemP: PropertyRoomCategoryEP => Int = _ => 0
}
