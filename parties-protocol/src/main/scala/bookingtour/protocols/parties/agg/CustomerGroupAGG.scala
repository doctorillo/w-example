package bookingtour.protocols.parties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem, SyncItem}
import bookingtour.protocols.parties.newTypes._
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, CustomerGroupId, PartyId, ProviderId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CustomerGroupAGG(
    id: CustomerGroupId,
    providerId: ProviderId,
    providerPartyId: PartyId,
    appId: AppId,
    appIdent: AppItem,
    ctxId: AppContextId,
    ctx: ContextItem,
    ctxCode: String,
    syncs: List[SyncItem],
    members: List[PartyId]
)

object CustomerGroupAGG {
  type Id = CustomerGroupId

  implicit final val itemR: CustomerGroupAGG => Id = _.id

  implicit final val itemP: CustomerGroupAGG => (PartyId, CustomerGroupAGG) = x => (x.providerPartyId, x)
}
