package bookingtour.protocols.orders.api

import bookingtour.protocols.core.values.Amount
import bookingtour.protocols.orders.newTypes.PlannerClientId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerExcursionItemClient(clientId: PlannerClientId, price: Amount)
