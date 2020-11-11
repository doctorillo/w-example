package bookingtour.protocols.parties.contexts

import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.SyncItem
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BusinessGroupUI(
    id: UUID,
    code: String,
    syncs: List[SyncItem],
    members: List[BusinessGroupMemberUI]
)

object BusinessGroupUI {
  type Id = UUID

  implicit final val itemR: BusinessGroupUI => Id = _.id

  implicit final val itemP: BusinessGroupUI => Int = _ => 0
}
