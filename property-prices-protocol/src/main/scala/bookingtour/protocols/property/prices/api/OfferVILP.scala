package bookingtour.protocols.property.prices.api

import bookingtour.protocols.interlook.source.newTypes.LookOfferId
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.property.prices.newTypes.OfferId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class OfferVILP(
    id: OfferId,
    supplierId: PartyId,
    syncId: LookOfferId,
    rules: List[OfferRuleVILP],
    special: Boolean
)

object OfferVILP {
  type Id = OfferId

  implicit final val itemR: OfferVILP => Id = _.id

  implicit final val itemP: OfferVILP => PartyId = _.supplierId
}
