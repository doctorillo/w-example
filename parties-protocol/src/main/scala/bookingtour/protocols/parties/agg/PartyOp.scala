package bookingtour.protocols.parties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookPartyId}
import bookingtour.protocols.parties.newTypes.PartyId
import cats.instances.all._
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(order)
final case class PartyOp(
    id: PartyId,
    sync: LookPartyId,
    group: Option[LookCustomerGroupId]
)

object PartyOp {
  final type Id = PartyId

  implicit final val itemR0: PartyOp => Id = _.id

  implicit final val itemP0: PartyOp => Int = _ => 0
}
