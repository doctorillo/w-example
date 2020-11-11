package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.aggregates.LabelAgg
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.newTypes.RegionId
import bookingtour.protocols.parties.newTypes.RegionId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RegionAgg(
    id: RegionId,
    country: CountryAgg,
    name: String,
    labels: List[LabelAgg],
    syncs: List[SyncItem]
)

object RegionAgg {
  type Id = RegionId

  implicit val itemR: RegionAgg => Id = _.id

  implicit final val itemP: RegionAgg => Int = _ => 0
}
