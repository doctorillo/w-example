package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{PartyId, PersonId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PersonAgg(
    id: PersonId,
    partyId: PartyId,
    firstName: String,
    lastName: String
)

object PersonAgg {
  type Id = PersonId

  implicit val itemR0: PersonAgg => Id = _.id

  implicit final val itemP0: PersonAgg => Int = _ => 0
}
