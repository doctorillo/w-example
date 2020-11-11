package bookingtour.protocols.properties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.LabelE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.properties.newTypes.{PropertyId, RoomCategoryId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomCategoryAGG(
    id: RoomCategoryId,
    propertyId: PropertyId,
    syncs: List[SyncItem],
    labels: List[LabelE]
)

object RoomCategoryAGG {
  type Id = RoomCategoryId

  implicit final val itemR: RoomCategoryAGG => Id = _.id

  implicit final val itemP: RoomCategoryAGG => PropertyId = _.propertyId
}
