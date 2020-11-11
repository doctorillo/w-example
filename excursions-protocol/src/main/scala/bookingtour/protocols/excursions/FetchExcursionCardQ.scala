package bookingtour.protocols.excursions

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.{CurrencyItem, LangItem, PointItem}
import bookingtour.protocols.parties.newTypes.{PartyId, PointId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class FetchExcursionCardQ(
    customerId: PartyId,
    lang: LangItem,
    pointId: PointId,
    pointCategory: PointItem,
    currency: CurrencyItem,
    dates: Ranges.Dates
)
