package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core.newtypes.quantities._
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId, LookRoomTypeId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyRoomTypeEP(id: RoomTypeKeyEP, roomType: RoomTypeEP)

object PropertyRoomTypeEP {
  type Id = RoomTypeKeyEP

  implicit final val itemR: PropertyRoomTypeEP => Id = _.id

  implicit final val itemP: PropertyRoomTypeEP => Int = _ => 0

  final case class Output(propertyId: Int, id: Int, name: String, mainPlaces: Int, extraPlaces: Int)

  implicit final val outputTransform: Output => PropertyRoomTypeEP = _.into[PropertyRoomTypeEP]
    .withFieldComputed(
      _.id,
      x => RoomTypeKeyEP(property = LookPartyId(x.propertyId), typeRoom = LookRoomTypeId(x.id))
    )
    .withFieldComputed(
      _.roomType,
      x =>
        RoomTypeEP(
          id = LookRoomTypeId(x.id),
          code = x.name,
          mainPlaces = PaxOnMain(x.mainPlaces),
          extraPlaces = PaxOnExtraBed(x.extraPlaces)
        )
    )
    .transform
}
