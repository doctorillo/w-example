package bookingtour.protocols.api.contexts.queries

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{ContextItem, ContextRoleItem}
import bookingtour.protocols.parties.newTypes.PartyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class FetchBusinessRelationQ(partyId: PartyId, ctx: ContextItem, role: ContextRoleItem)
