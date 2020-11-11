package bookingtour.protocols.parties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem, SyncItem}
import bookingtour.protocols.parties.newTypes._
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, PartyId, ProviderId, SupplierGroupId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class SupplierGroupAgg(
    id: SupplierGroupId,
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

object SupplierGroupAgg {
  type Id = SupplierGroupId

  implicit final val itemR: SupplierGroupAgg => Id = _.id

  implicit final val itemP: SupplierGroupAgg => (PartyId, SupplierGroupAgg) = x => (x.providerPartyId, x)
}
