package bookingtour.protocols.parties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId, PartyId, ProviderId, SupplierGroupId}
import cats.instances.all._
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(order)
final case class SupplierGroupOp(
    id: SupplierGroupId,
    party: PartyId,
    partySync: LookPartyId,
    app: AppId,
    ctx: AppContextId,
    context: ContextItem,
    provider: ProviderId
)

object SupplierGroupOp {
  final type Id = SupplierGroupId

  implicit final val itemR0: SupplierGroupOp => Id = _.id

  implicit final val itemP0: SupplierGroupOp => Int = _ => 0
}
