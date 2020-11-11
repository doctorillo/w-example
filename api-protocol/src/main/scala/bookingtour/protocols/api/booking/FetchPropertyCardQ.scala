package bookingtour.protocols.api.booking

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.{LangItem, PointItem}
import bookingtour.protocols.parties.api.queries.QueryGroup
import bookingtour.protocols.parties.newTypes.{PartyId, PointId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class FetchPropertyCardQ(
    customerId: PartyId,
    lang: LangItem,
    pointId: PointId,
    pointCategory: PointItem,
    dates: Ranges.Dates,
    group: QueryGroup
)
