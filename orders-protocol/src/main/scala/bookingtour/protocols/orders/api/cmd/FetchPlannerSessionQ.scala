package bookingtour.protocols.orders.api.cmd

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.SolverId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class FetchPlannerSessionQ(solverId: SolverId)
