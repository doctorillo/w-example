package bookingtour.protocols.orders.api

import java.time.LocalDate

import bookingtour.protocols.core._
import bookingtour.protocols.orders.newTypes.{PlannerExcursionId, PlannerExcursionItemId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerExcursion(
    id: PlannerExcursionId,
    queryGroup: PlannerQueryClientGroup,
    dates: List[LocalDate],
    items: List[PlannerExcursionVariantDate],
    selected: List[PlannerExcursionItemId],
    variantCount: Int,
    excursionDates: List[PlannerExcursionDate],
    filter: PlannerExcursionFilterParams,
    maxItems: Int
)
