package bookingtour.protocols.interlook.source.parties

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookPartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CustomerGroupEP(
    id: CustomerGroupKeyEP,
    name: String,
    members: List[PartnerEP]
)

object CustomerGroupEP {
  type Id = CustomerGroupKeyEP

  implicit final val itemR: CustomerGroupEP => Id = _.id

  implicit final val itemP: CustomerGroupEP => Int = _ => 0

  implicit final class CustomerGroupEPOps(private val self: CustomerGroupEP) {
    def party: LookPartyId                 = self.id.partyId
    def group: Option[LookCustomerGroupId] = self.id.groupId
    def context: ContextItem               = self.id.contextItem
  }
}
