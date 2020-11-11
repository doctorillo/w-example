package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.aggregates.LabelAgg
import bookingtour.protocols.core.values.enumeration.SyncItem._
import bookingtour.protocols.core.values.enumeration.{PointItem, SyncItem}
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.interlook.source.newTypes.LookPickupPointId
import bookingtour.protocols.parties.newTypes.{CityId, PickupPointId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PickupPointAgg(
    id: PickupPointId,
    cityId: CityId,
    name: String,
    category: PointItem,
    location: GPoint,
    labels: List[LabelAgg],
    syncs: List[SyncItem]
)
object PickupPointAgg {
  type Id = PickupPointId

  implicit val itemR0: PickupPointAgg => Id = _.id

  implicit final val itemP0: PickupPointAgg => CityId = _.cityId

  implicit final val itemP1: PickupPointAgg => Int = _ => 0

  implicit final class PickupPointAggOps(private val self: PickupPointAgg) {
    def pointInterLook: Option[LookPickupPointId] =
      askInterLook(self.syncs)
        .map(x => LookPickupPointId(x.id))
  }
}
