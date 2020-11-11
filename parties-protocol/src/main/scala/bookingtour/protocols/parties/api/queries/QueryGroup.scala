package bookingtour.protocols.parties.api.queries

import bookingtour.protocols.core.values.enumeration.PersonGroupItem
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class QueryGroup(
    category: PersonGroupItem,
    rooms: List[QueryRoom]
)
