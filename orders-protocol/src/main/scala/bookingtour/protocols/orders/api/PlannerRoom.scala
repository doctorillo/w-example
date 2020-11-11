package bookingtour.protocols.orders.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.Position
import bookingtour.protocols.orders.newTypes.{PlannerAccommodationItemId, PlannerClientId, PlannerRoomId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerRoom(
    id: PlannerRoomId,
    clients: List[PlannerClientId],
    variants: List[PlannerAccommodationItem],
    selected: Option[PlannerAccommodationItemId],
    position: Position
)
