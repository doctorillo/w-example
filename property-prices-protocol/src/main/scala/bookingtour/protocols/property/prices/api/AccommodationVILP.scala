package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.BedItem
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.property.prices.newTypes.AccommodationId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AccommodationVILP(
    id: AccommodationId,
    supplier: PartyId,
    age: Ranges.Ints,
    place: BedItem
)

object AccommodationVILP {
  type Id = AccommodationId

  implicit final val itemR0: AccommodationVILP => Id = _.id

  implicit final val itemP0: AccommodationVILP => Int = _ => 0
}
