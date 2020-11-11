package bookingtour.protocols.orders.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.orders.newTypes.PlannerSessionPointId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerSessionPoint(
    id: PlannerSessionPointId,
    point: PointOption,
    dates: Ranges.Dates,
    property: Option[PlannerProperty],
    transfer: Option[Int],
    excursion: Option[PlannerExcursion],
    variantCount: Int
)
