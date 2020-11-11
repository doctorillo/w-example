package bookingtour.protocols.interlook.source.parties

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class SupplierGroupEP(
    id: SupplierGroupKeyEP,
    members: List[LookPartyId]
)

object SupplierGroupEP {
  type Id = SupplierGroupKeyEP

  implicit final val itemR: SupplierGroupEP => Id = _.id

  implicit final val itemP: SupplierGroupEP => Int = _ => 0

  implicit final class SupplierGroupEPOps(private val self: SupplierGroupEP) {
    def party: LookPartyId   = self.id.partyId
    def context: ContextItem = self.id.contextItem
  }
}
