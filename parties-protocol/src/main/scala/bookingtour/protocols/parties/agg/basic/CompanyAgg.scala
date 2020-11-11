package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.api.PartyValue
import bookingtour.protocols.parties.newTypes.{CompanyId, PartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CompanyAgg(
    id: CompanyId,
    party: PartyAgg,
    address: AddressAgg,
    code: String,
    name: String
)

object CompanyAgg {
  type Id = CompanyId

  implicit final val itemR0: CompanyAgg => Id = _.id

  implicit final val itemP: CompanyAgg => Int = _ => 0

  implicit final class CompanyAggOps(private val self: CompanyAgg) {
    def partyId: PartyId              = self.party.id
    def lookSync: Option[LookPartyId] = self.party.lookSync
    def toPartyValue: PartyValue      = PartyValue(self.party.id, self.name)
  }
}
