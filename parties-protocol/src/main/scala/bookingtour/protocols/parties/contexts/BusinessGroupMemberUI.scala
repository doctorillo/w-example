package bookingtour.protocols.parties.contexts

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.PartyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BusinessGroupMemberUI(partyId: PartyId, name: String)
