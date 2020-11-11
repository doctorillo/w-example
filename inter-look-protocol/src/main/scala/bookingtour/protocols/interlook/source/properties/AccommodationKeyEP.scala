package bookingtour.protocols.interlook.source.properties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookAccommodationId, LookPartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AccommodationKeyEP(
    property: LookPartyId,
    accommodation: LookAccommodationId
)

object AccommodationKeyEP {
  type Id = AccommodationKeyEP

  implicit final val itemR: AccommodationKeyEP => Id = x => x

  implicit final val itemP: AccommodationKeyEP => Int = _ => 0
}
