package bookingtour.protocols.parties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookPartyId}
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, CustomerGroupId, PartyId, ProviderId}
import cats.instances.all._
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(order)
final case class CustomerGroupOp(
    id: CustomerGroupId,
    sync: LookCustomerGroupId,
    party: PartyId,
    partySync: LookPartyId,
    app: AppId,
    ctx: AppContextId,
    contextItem: ContextItem,
    provider: ProviderId
)

object CustomerGroupOp {
  final type Id = CustomerGroupId

  implicit final val itemR0: CustomerGroupOp => Id = _.id

  implicit final val itemP0: CustomerGroupOp => Int = _ => 0
}
