package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.interlook.source.newTypes.{LookAccommodationId, LookPartyId}
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.property.prices.newTypes.AccommodationId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{PaxOnExtraBed, PaxOnMain}
import bookingtour.protocols.parties.api.queries.QueryGuest
import cats.syntax.order._
import Ranges.Ints._
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order, loggable)
final case class AccommodationOp(
    id: AccommodationId,
    accommodationSync: LookAccommodationId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    code: String,
    onMain: PaxOnMain,
    onExb: PaxOnExtraBed,
    guests: List[AccommodationGuestOp]
)

object AccommodationOp {
  final type Id = AccommodationId

  implicit final val itemR0: AccommodationOp => Id = _.id

  implicit final val itemP0: AccommodationOp => Int = _ => 0

  /* implicit final val toVilp: AccommodationOp => AccommodationVILP =
    _.into[AccommodationVILP].transform*/

  implicit final class AccommodationOpOps(private val self: AccommodationOp) {
    def condition(guests: List[QueryGuest]): Boolean = {
      val (result, _) = guests
        .foldLeft((guests.size === self.guests.size, self.guests)) {
          case ((r, xs), x) =>
            if (!r) {
              (r, List.empty)
            } else {
              xs.find(z => (x.age.isEmpty && z.age.contains(25)) || x.age.exists(age => z.age.contains(age))) match {
                case Some(chunk) =>
                  (true, xs.filterNot(_ === chunk))

                case None =>
                  (false, List.empty)

              }
            }
        }
      result
    }
  }
}
