package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.aggregates.LabelAgg
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.newTypes.CountryId
import bookingtour.protocols.parties.newTypes.CountryId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CountryAgg(
    id: CountryId,
    name: String,
    labels: List[LabelAgg],
    syncs: List[SyncItem]
)

object CountryAgg {
  type Id = CountryId

  implicit val countryAggR: CountryAgg => Id = _.id

  implicit final val countryAggPart: CountryAgg => Int = _ => 0
}
