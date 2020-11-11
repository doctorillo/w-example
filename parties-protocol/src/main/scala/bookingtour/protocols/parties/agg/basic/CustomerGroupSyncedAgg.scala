package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.SyncItem
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
final case class CustomerGroupSyncedAgg(
    id: CustomerGroupId,
    provider: ProviderAgg,
    code: Option[String],
    notes: Option[String],
    syncs: List[SyncItem]
)
object CustomerGroupSyncedAgg {
  type Id = CustomerGroupId

  implicit val itemR0: CustomerGroupSyncedAgg => Id = _.id

  implicit final val itemP0: CustomerGroupSyncedAgg => ProviderId = _.provider.id

  implicit final val itemP1: CustomerGroupSyncedAgg => Int = _ => 0
}
