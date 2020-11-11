package bookingtour.protocols.api.booking

import java.util.UUID

import bookingtour.protocols.core.values.enumeration.PersonGroupItem
import cats.data.Chain
import cats.instances.uuid._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PersonGroupSP(
    id: UUID,
    category: PersonGroupItem,
    rooms: Chain[RoomSP]
)
