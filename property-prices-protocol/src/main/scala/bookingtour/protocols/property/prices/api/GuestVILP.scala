package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.BedItem
import bookingtour.protocols.interlook.source.newTypes.{LookAccommodationAgeId, LookAccommodationId}
import bookingtour.protocols.property.prices.newTypes.{AccommodationId, GuestId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class GuestVILP(
    guestId: GuestId,
    age: Ranges.Ints,
    accommodationId: AccommodationId,
    placeCategory: BedItem,
    accommodationSyncId: LookAccommodationId,
    accommodationRefSyncId: Option[LookAccommodationAgeId]
)

object GuestVILP {
  type Id = GuestId

  implicit final val itemR: GuestVILP => Id = _.guestId

  implicit final val itemP: GuestVILP => AccommodationId = _.accommodationId
}
