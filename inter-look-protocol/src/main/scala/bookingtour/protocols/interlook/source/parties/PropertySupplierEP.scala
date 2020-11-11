package bookingtour.protocols.interlook.source.parties

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertySupplierEP(
    id: LookPartyId,
    customerId: LookPartyId
)

object PropertySupplierEP {
  type Id = LookPartyId

  implicit final val itemR: PropertySupplierEP => Id = _.id

  implicit final val itemP: PropertySupplierEP => Int = _ => 0
}
