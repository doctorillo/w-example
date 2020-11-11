package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, ProviderId}
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, ProviderId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CustomerGroupAgg(
    id: CustomerGroupId,
    provider: ProviderAgg,
    code: Option[String],
    notes: Option[String]
)

object CustomerGroupAgg {
  type Id = CustomerGroupId

  implicit val itemR0: CustomerGroupAgg => Id = _.id

  implicit final val itemP0: CustomerGroupAgg => ProviderId = _.provider.id

  implicit final val itemP1: CustomerGroupAgg => Int = _ => 0
}
