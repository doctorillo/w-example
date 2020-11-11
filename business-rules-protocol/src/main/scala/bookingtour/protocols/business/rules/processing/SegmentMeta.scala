package bookingtour.protocols.business.rules.processing

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SegmentMeta(
    group: CustomerGroupId,
    parent: Option[CustomerGroupId],
    supplier: PartyId,
    customer: PartyId
)
