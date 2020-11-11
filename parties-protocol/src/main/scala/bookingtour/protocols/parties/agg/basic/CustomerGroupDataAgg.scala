package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem, SyncItem}
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookPartyId}
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, CustomerGroupId, PartyId, ProviderId}
import bookingtour.protocols.parties.values.CustomerGroupPREP
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CustomerGroupDataAgg(
    id: CustomerGroupId,
    provider: ProviderAgg,
    code: Option[String],
    notes: Option[String],
    syncs: List[SyncItem],
    members: List[CompanyAgg]
)

object CustomerGroupDataAgg {
  type Id = CustomerGroupId

  implicit final val itemR0: CustomerGroupDataAgg => Id = _.id

  implicit final val itemP0: CustomerGroupDataAgg => ProviderId = _.provider.id

  implicit final class CustomerGroupDataAggOps(private val self: CustomerGroupDataAgg) {
    def app: AppId                         = self.provider.app
    def appIdent: AppItem                  = self.provider.appIdent
    def appContext: AppContextId           = self.provider.appContext
    def context: ContextItem               = self.provider.context
    def party: PartyId                     = self.provider.party
    def partyLookSync: Option[LookPartyId] = self.provider.lookSync
    def groupLookSync: Option[LookCustomerGroupId] =
      self.syncs.flatMap(CustomerGroupPREP.fromSyncItem(_).toList).headOption
  }
}
