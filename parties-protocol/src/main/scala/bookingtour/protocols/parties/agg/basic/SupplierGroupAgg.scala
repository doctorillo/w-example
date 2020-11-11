package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{ProviderId, SupplierGroupId}
import bookingtour.protocols.parties.newTypes.{ProviderId, SupplierGroupId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SupplierGroupAgg(
    id: SupplierGroupId,
    provider: ProviderAgg,
    code: Option[String],
    notes: Option[String]
)

object SupplierGroupAgg {
  type Id = SupplierGroupId

  implicit val itemR0: SupplierGroupAgg => Id = _.id

  implicit final val itemP0: SupplierGroupAgg => ProviderId = _.provider.id
}
