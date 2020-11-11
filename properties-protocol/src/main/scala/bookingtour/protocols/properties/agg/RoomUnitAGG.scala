package bookingtour.protocols.properties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.DescriptionE
import bookingtour.protocols.properties.newTypes.{PropertyId, RoomUnitId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomUnitAGG(
    id: RoomUnitId,
    typ: RoomTypeAGG,
    cat: RoomCategoryAGG,
    descriptions: List[DescriptionE]
)

object RoomUnitAGG {
  type Id = RoomUnitId

  implicit final val itemR: RoomUnitAGG => Id = _.id

  implicit final val itemP: RoomUnitAGG => PropertyId = _.typ.propertyId
}
