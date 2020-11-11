package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.parties.newTypes.PartyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PartyAgg(id: PartyId, syncs: List[SyncItem])

object PartyAgg {
  type Id = PartyId

  implicit final val itemR0: PartyAgg => Id = _.id

  implicit final val itemP0: PartyAgg => Int = _ => 0

  implicit final class PartyAggOps(private val self: PartyAgg) extends AnyVal {
    def lookSync: Option[LookPartyId] = SyncItem.askInterLook(self.syncs).map(_.id)
  }
}
