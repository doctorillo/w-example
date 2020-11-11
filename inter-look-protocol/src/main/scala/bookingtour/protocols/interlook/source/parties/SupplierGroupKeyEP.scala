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
final case class SupplierGroupKeyEP(partyId: LookPartyId, contextItem: ContextItem)

object SupplierGroupKeyEP {
  type Id = SupplierGroupKeyEP

  implicit final val itemR: SupplierGroupKeyEP => Id = x => x
}
