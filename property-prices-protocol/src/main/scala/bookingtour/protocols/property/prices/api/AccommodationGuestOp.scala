package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.Position
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.BedItem
import bookingtour.protocols.property.prices.newTypes.{AccommodationGuestId, GuestId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order, loggable)
final case class AccommodationGuestOp(
    id: AccommodationGuestId,
    guest: GuestId,
    age: Ranges.Ints,
    place: BedItem,
    placePosition: Position
)

object AccommodationGuestOp {
  final type Id = AccommodationGuestId

  implicit final val itemR0: AccommodationGuestOp => Id = _.id

  implicit final val itemP0: AccommodationGuestOp => Int = _ => 0

  /*implicit final val toVilp: AccommodationGuestOp => AccommodationVILP = _.into[AccommodationVILP]
    .withFieldComputed(_.id, _.accommodation)
    .withFieldComputed(_.id, _.accommodation)
    .transform*/
}
