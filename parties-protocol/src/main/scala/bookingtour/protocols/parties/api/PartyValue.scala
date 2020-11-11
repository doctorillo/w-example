package bookingtour.protocols.parties.api

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.parties.newTypes.PartyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PartyValue(id: PartyId, name: String)

object PartyValue {
  type Id = PartyId

  implicit val itemR: PartyValue => Id = _.id

  implicit final val itemPart: PartyValue => Int = _ => 0
}
