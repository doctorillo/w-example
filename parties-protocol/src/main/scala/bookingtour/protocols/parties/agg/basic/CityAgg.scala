package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.aggregates.LabelAgg
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.newTypes.CityId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CityAgg(
    id: CityId,
    region: RegionAgg,
    name: String,
    labels: List[LabelAgg],
    syncs: List[SyncItem]
)

object CityAgg {
  type Id = CityId

  implicit val itemR0: CityAgg => Id = _.id

  implicit final val itemP0: CityAgg => Int = _ => 0
}
