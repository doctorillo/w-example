package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.SyncItem._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem}
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookPartyId}
import bookingtour.protocols.parties.newTypes.{PartyId, ProviderId}
import cats.instances.all._
import cats.syntax.order._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class ProviderDataAgg(
    id: ProviderId,
    company: CompanyAgg,
    ctx: AppContextAgg,
    suppliers: List[SupplierGroupDataAgg],
    customers: List[CustomerGroupDataAgg]
)

object ProviderDataAgg {
  type Id = ProviderId

  implicit final val itemR0: ProviderDataAgg => Id = _.id

  implicit final val itemP0: ProviderDataAgg => Int = _ => 0

  implicit final class ProviderDataAggOps(private val self: ProviderDataAgg) {
    def party: PartyId                      = self.company.party.id
    def partyInterLook: Option[LookPartyId] = self.company.lookSync
    def appItem: AppItem                    = self.ctx.appIdent
    def context: ContextItem                = self.ctx.ctxType
    def isAccommodationPartner: Boolean =
      appItem === AppItem.Partner && context === ContextItem.Accommodation
    def isExcursionPartner: Boolean =
      appItem === AppItem.Partner && context === ContextItem.Excursion
    def isTransferPartner: Boolean =
      appItem === AppItem.Partner && context === ContextItem.Transfer
    def hasExcursion(id: PartyId): Boolean = isExcursionPartner && self.company.party.id === id
    def excursionCustomerGroup(id: LookCustomerGroupId): Option[CustomerGroupDataAgg] =
      self.customers.find(x => askInterLook(x.syncs).exists(_.id === id.x))
  }
}
