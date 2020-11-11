package bookingtour.protocols.api.contexts.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.RoleItem
import bookingtour.protocols.parties.newTypes.{PartyId, UserId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class ContextPartyA(
    userId: UserId,
    businessPartyId: PartyId,
    businessParty: String,
    roles: List[ContextRoleA],
    securities: List[RoleItem]
)
