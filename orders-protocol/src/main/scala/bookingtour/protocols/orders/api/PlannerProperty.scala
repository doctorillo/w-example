package bookingtour.protocols.orders.api

import java.util.UUID

import bookingtour.protocols.core.values.enumeration.PriceViewMode
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerProperty(
    id: UUID,
    queryGroup: PlannerQueryGroup,
    rooms: List[PlannerRoom],
    variantCount: Int,
    filter: PlannerPropertyFilterParams,
    priceView: PriceViewMode,
    maxItems: Int
)
