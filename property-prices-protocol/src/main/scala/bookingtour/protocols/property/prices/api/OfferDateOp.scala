package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.interlook.source.newTypes.{LookLinkServiceId, LookOfferId, LookPartyId}
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.property.prices.newTypes.{OfferDateId, OfferId}
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
final case class OfferDateOp(
    id: OfferDateId,
    offer: OfferId,
    offerSync: LookOfferId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    dates: Ranges.Dates,
    linkService: LookLinkServiceId,
    duration: Ranges.Ints
)

object OfferDateOp {
  final type Id = OfferDateId

  implicit final val itemR0: OfferDateOp => Id = _.id

  implicit final val itemP0: OfferDateOp => Int = _ => 0

  final val itemP1: OfferDateOp => LookPartyId = _.supplierSync
}
