package bookingtour.protocols.parties.contexts

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem}
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, PartyId, ProviderId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BusinessContextUI(
    id: AppContextId,
    appId: AppId,
    appIdent: AppItem,
    code: String,
    cxtType: ContextItem,
    partyId: PartyId,
    providerId: ProviderId,
    suppliers: List[BusinessGroupUI],
    customers: List[BusinessGroupUI]
)

object BusinessContextUI {
  type Id = AppContextId

  implicit val businessContextUIR: BusinessContextUI => Id = _.id

  implicit final val businessContextUIPart: BusinessContextUI => (PartyId, BusinessContextUI) =
    x => (x.partyId, x)
}
