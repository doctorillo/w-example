package bookingtour.protocols.api.parties

import java.util.UUID

import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem, ContextRoleItem}

/**
  * © Alexey Toroshchin 2019.
  */
final case class FetchRelations(
    partyId: UUID,
    app: AppItem,
    ctx: ContextItem,
    role: ContextRoleItem
)
