package bookingtour.protocols.orders.api

import java.time.LocalDate

import bookingtour.protocols.core.values.enumeration.ExcursionTagItem
import bookingtour.protocols.core._
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerExcursionFilterParams(
    name: Option[String],
    price: List[Double],
    tags: List[ExcursionTagItem],
    viewDates: List[LocalDate]
)
