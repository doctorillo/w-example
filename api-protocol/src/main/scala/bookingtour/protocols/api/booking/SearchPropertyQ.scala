package bookingtour.protocols.api.booking

import java.util.UUID

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.{LangItem, PointItem}
import cats.instances.uuid._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SearchPropertyQ(
    lang: LangItem,
    pointId: UUID,
    pointCategory: PointItem,
    dates: Ranges.Dates,
    persons: PersonGroupSP
)
