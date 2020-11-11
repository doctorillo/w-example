package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookRoomCategoryId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomCategoryKeyEP(property: LookPartyId, categoryRoom: LookRoomCategoryId)

object RoomCategoryKeyEP {
  type Id = RoomCategoryKeyEP

  implicit final val itemR: RoomCategoryKeyEP => Id = x => x

  implicit final val itemP: RoomCategoryKeyEP => Int = _ => 0
}
