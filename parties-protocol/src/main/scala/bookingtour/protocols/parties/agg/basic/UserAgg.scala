package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{RoleItem, SyncItem}
import bookingtour.protocols.parties.api.ui.WorkspaceUI
import bookingtour.protocols.parties.newTypes.UserId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class UserAgg(
    id: UserId,
    provider: ProviderDataAgg,
    syncs: List[SyncItem],
    roles: List[RoleItem]
)

object UserAgg {
  type Id = UserId

  implicit final val itemR0: UserAgg => Id = _.id

  implicit final val toWorkspaceUI: UserAgg => WorkspaceUI = _.into[WorkspaceUI]
    .withFieldComputed(_.userId, _.id)
    .withFieldComputed(_.businessPartyId, _.provider.company.party.id)
    .withFieldComputed(_.businessParty, _.provider.company.name)
    .withFieldComputed(_.securities, _.roles)
    .transform
}
