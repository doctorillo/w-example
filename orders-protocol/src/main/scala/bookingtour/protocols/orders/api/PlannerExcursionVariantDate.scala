package bookingtour.protocols.orders.api

import java.time.LocalDate

import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import bookingtour.protocols.core._
import cats.instances.all._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerExcursionVariantDate(
    date: LocalDate,
    variants: List[PlannerExcursionItem],
    variantCount: Int
)
