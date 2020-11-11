package bookingtour.protocols.orders.api.cmd

import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.api.PlannerSession
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class UpsertPlannerSessionQ(planner: PlannerSession)
