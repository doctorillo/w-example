package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookRoomTypeId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomTypeKeyEP(property: LookPartyId, typeRoom: LookRoomTypeId)

object RoomTypeKeyEP {
  type Id = RoomTypeKeyEP

  implicit final val itemR: RoomTypeKeyEP => Id = x => x

  implicit final val itemP: RoomTypeKeyEP => Int = _ => 0
}
