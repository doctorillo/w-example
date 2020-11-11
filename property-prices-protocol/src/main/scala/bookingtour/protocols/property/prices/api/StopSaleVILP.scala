package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.{Month, Year}
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.property.prices.agg
import bookingtour.protocols.property.prices.agg.{RoomTargetUnits, StopSaleVKey}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class StopSaleVILP(
    target: RoomTargetUnits,
    month: Month,
    year: Year,
    dates: List[Ranges.Dates]
)

object StopSaleVILP {
  type Id = StopSaleVILP

  implicit final val itemR0: StopSaleVILP => Id = x => x

  implicit final val itemP0: StopSaleVILP => StopSaleVKey = x =>
    StopSaleVKey(propertyId = x.target.propertyId, month = x.month, year = x.year)
}
