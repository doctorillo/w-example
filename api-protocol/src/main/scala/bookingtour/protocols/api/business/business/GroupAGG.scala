package bookingtour.protocols.api.business.business

import java.util.UUID

import bookingtour.protocols.core.values.Ranges
import cats.implicits._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class GroupAGG(
    id: UUID,
    providerId: UUID,
    code: Option[String],
    operationDates: Ranges.Dates,
    lifeDates: Ranges.Dates,
    notes: Option[String]
)
