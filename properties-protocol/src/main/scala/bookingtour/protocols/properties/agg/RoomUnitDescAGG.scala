package bookingtour.protocols.properties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.DescriptionE
import bookingtour.protocols.properties.newTypes.{RoomCategoryId, RoomTypeId, RoomUnitId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomUnitDescAGG(
    id: RoomUnitId,
    typId: RoomTypeId,
    catId: RoomCategoryId,
    descriptions: List[DescriptionE]
)

object RoomUnitDescAGG {
  type Id = RoomUnitId

  implicit final val itemR: RoomUnitDescAGG => Id = _.id

  implicit final val itemP: RoomUnitDescAGG => Int = _ => 0
}
