package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{PaxOnExtraBed, PaxOnMain}
import bookingtour.protocols.interlook.source.newTypes.LookRoomTypeId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomTypeEP(
    id: LookRoomTypeId,
    code: String,
    mainPlaces: PaxOnMain,
    extraPlaces: PaxOnExtraBed
)

object RoomTypeEP {
  type Id = LookRoomTypeId

  implicit final val itemR: RoomTypeEP => Id = _.id

  implicit final val itemP: RoomTypeEP => Int = _ => 0

  final case class Output(
      id: Int,
      code: String,
      mainPlaces: Int,
      extraPlaces: Int
  )

  implicit final val outputTransform: Output => RoomTypeEP = _.into[RoomTypeEP]
    .withFieldComputed(_.id, x => LookRoomTypeId(x.id))
    .withFieldComputed(_.mainPlaces, x => PaxOnMain(x.mainPlaces))
    .withFieldComputed(_.extraPlaces, x => PaxOnExtraBed(x.extraPlaces))
    .transform
}
