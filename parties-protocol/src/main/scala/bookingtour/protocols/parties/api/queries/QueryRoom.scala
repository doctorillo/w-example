package bookingtour.protocols.parties.api.queries

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.Position
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class QueryRoom(guests: List[QueryGuest], position: Position)
