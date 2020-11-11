package bookingtour.protocols.parties.contexts

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{RoleItem, SyncItem}
import bookingtour.protocols.parties.newTypes.{PartyId, UserId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BusinessUserUI(
    id: UserId,
    syncs: List[SyncItem],
    company: BusinessPartyUI,
    roles: List[RoleItem]
)

object BusinessUserUI {
  type Id = UserId

  implicit val businessUserUIR: BusinessUserUI => Id = _.id

  implicit final val businessUserUIPart: BusinessUserUI => (PartyId, BusinessUserUI) = x => (x.company.partyId, x)
}
