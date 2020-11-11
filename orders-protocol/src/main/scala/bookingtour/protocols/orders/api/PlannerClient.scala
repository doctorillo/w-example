package bookingtour.protocols.orders.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Age, Position}
import bookingtour.protocols.orders.newTypes.PlannerClientId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerClient(
    id: PlannerClientId,
    age: Option[Age],
    meta: PlannerClientMeta,
    position: Position
)
