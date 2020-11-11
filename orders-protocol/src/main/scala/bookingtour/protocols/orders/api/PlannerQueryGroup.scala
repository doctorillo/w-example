package bookingtour.protocols.orders.api

import java.util.UUID

import bookingtour.protocols.core.values.enumeration.ClientGroupItem
import bookingtour.protocols.parties.api.queries.QueryRoom
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerQueryGroup(id: UUID, category: ClientGroupItem, rooms: List[QueryRoom])
