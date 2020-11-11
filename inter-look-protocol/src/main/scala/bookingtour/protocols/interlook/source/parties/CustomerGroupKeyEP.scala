package bookingtour.protocols.interlook.source.parties

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookPartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class CustomerGroupKeyEP(
    partyId: LookPartyId,
    groupId: Option[LookCustomerGroupId],
    contextItem: ContextItem
)

object CustomerGroupKeyEP {
  type Id = CustomerGroupKeyEP

  implicit final val itemR: CustomerGroupKeyEP => Id = x => x
}
