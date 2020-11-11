package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.LookRoomCategoryId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomCategoryEP(
    id: LookRoomCategoryId,
    code: String
)

object RoomCategoryEP {
  type Id = LookRoomCategoryId

  implicit final val itemR: RoomCategoryEP => Id = _.id

  implicit final val itemP: RoomCategoryEP => Int = _ => 0

  final case class Output(id: Int, code: String)

  implicit final val outputTransform: Output => RoomCategoryEP = _.into[RoomCategoryEP]
    .withFieldComputed(_.id, x => LookRoomCategoryId(x.id))
    .transform
}
