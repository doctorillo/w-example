package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, ContextItem}
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, PartyId, ProviderId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class ProviderAgg(id: ProviderId, company: CompanyAgg, ctx: AppContextAgg)

object ProviderAgg {
  type Id = ProviderId

  implicit final val itemR: ProviderAgg => Id = _.id

  implicit final val itemP: ProviderAgg => AppContextId = _.ctx.id

  implicit final class ProviderAggOps(private val self: ProviderAgg) {
    def app: AppId                    = self.ctx.appId
    def appIdent: AppItem             = self.ctx.appIdent
    def appContext: AppContextId      = self.ctx.id
    def context: ContextItem          = self.ctx.ctxType
    def party: PartyId                = self.company.party
    def lookSync: Option[LookPartyId] = self.company.lookSync
  }
}
