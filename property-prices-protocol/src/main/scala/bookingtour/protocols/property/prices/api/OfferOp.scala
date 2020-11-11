package bookingtour.protocols.property.prices.api

import bookingtour.protocols.interlook.source.newTypes.{LookOfferId, LookPartyId}
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.property.prices.newTypes.OfferId
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import tofu.logging.derivation.{loggable}

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order, loggable)
final case class OfferOp(
    id: OfferId,
    offerSync: LookOfferId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    special: Boolean
)

object OfferOp {
  final type Id = OfferId

  implicit final val itemR0: OfferOp => Id = _.id

  implicit final val itemP0: OfferOp => Int = _ => 0
}
