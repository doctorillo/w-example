package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.property.prices.newTypes.{OfferDateId, OfferId, PropertyProviderId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
case class PriceDateVILP(
    id: OfferDateId,
    propertyProviderId: PropertyProviderId,
    offerId: OfferId,
    dates: Ranges.Dates,
    stayDuration: Ranges.Ints
)

object PriceDateVILP {
  type Id = OfferDateId

  implicit final val itemR: PriceDateVILP => Id = _.id

  implicit final val itemP: PriceDateVILP => PropertyProviderId = _.propertyProviderId
}
