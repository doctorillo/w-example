package bookingtour.protocols.parties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookPartyId}
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, PartyId, ProviderId}
import cats.instances.all._
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(order)
final case class ProviderOp(
    id: ProviderId,
    party: PartyId,
    partySync: LookPartyId,
    partyGroup: Option[LookCustomerGroupId],
    app: AppId,
    ctx: AppContextId,
    context: ContextItem
)

object ProviderOp {
  final type Id = ProviderId

  implicit final val itemR0: ProviderOp => Id = _.id

  implicit final val itemP0: ProviderOp => Int = _ => 0
}
