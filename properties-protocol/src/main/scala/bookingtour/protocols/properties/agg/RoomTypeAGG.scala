package bookingtour.protocols.properties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{PaxOnExtraBed, PaxOnMain}
import bookingtour.protocols.core.values.db.LabelE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.properties.newTypes.{PropertyId, RoomTypeId}
import bookingtour.protocols.properties.newTypes.{PropertyId, RoomTypeId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RoomTypeAGG(
    id: RoomTypeId,
    propertyId: PropertyId,
    syncs: List[SyncItem],
    onMain: PaxOnMain,
    onExb: PaxOnExtraBed,
    labels: List[LabelE]
)

object RoomTypeAGG {
  type Id = RoomTypeId

  implicit final val itemR: RoomTypeAGG => Id = _.id

  implicit final val itemP: RoomTypeAGG => PropertyId = _.propertyId
}
