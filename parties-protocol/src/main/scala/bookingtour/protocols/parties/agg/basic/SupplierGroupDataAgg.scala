package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem}
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, PartyId, ProviderId, SupplierGroupId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SupplierGroupDataAgg(
    id: SupplierGroupId,
    provider: ProviderAgg,
    code: Option[String],
    notes: Option[String],
    members: List[PartyId]
)

object SupplierGroupDataAgg {
  type Id = SupplierGroupId

  implicit final val itemR0: SupplierGroupDataAgg => Id = _.id

  implicit final val itemP0: SupplierGroupDataAgg => ProviderId = _.provider.id

  implicit final val itemP1: SupplierGroupDataAgg => Int = _ => 0

  implicit final class SupplierGroupDataAggOps(private val self: SupplierGroupDataAgg) {
    def app: AppId                    = self.provider.app
    def appIdent: AppItem             = self.provider.appIdent
    def appContext: AppContextId      = self.provider.appContext
    def context: ContextItem          = self.provider.context
    def party: PartyId                = self.provider.party
    def lookSync: Option[LookPartyId] = self.provider.lookSync
  }
}
