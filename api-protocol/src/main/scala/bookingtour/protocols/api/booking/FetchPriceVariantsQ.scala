package bookingtour.protocols.api.booking

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.parties.api.queries.QueryGroup
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.properties.newTypes.PropertyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class FetchPriceVariantsQ(
    customerId: PartyId,
    propertyId: PropertyId,
    lang: LangItem,
    dates: Ranges.Dates,
    group: QueryGroup
)
