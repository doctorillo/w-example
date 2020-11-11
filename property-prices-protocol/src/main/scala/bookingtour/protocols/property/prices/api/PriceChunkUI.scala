package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.Nights
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.property.prices.newTypes.{OfferDateId, OfferId, PriceId, TariffId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PriceChunkUI(
    id: PriceId,
    offerId: OfferId,
    tariffId: TariffId,
    stayDuration: Ranges.Ints,
    priceDateId: OfferDateId,
    dates: Ranges.Dates,
    rules: List[OfferRuleVILP],
    nights: Nights,
    price: Amount,
    amount: Amount,
    discount: Option[Amount],
    specialOffer: Boolean
)

object PriceChunkUI {
  type Id = PriceId

  implicit final val itemR: PriceChunkUI => Id = _.id

  implicit final val itemP: PriceChunkUI => OfferId = _.offerId
}
