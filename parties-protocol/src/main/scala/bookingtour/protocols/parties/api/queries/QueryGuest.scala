package bookingtour.protocols.parties.api.queries

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Age, Position}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class QueryGuest(
    age: Option[Age] = None,
    boarding: Option[String] = None,
    position: Position
)
