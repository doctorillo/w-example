package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.Nights
import bookingtour.protocols.core.values.{ Amount, Ranges }
import bookingtour.protocols.property.prices.newTypes.{ OfferDateId, PriceId }
import derevo.cats.order
import derevo.circe.{ decoder, encoder }
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
 * © Alexey Toroshchin 2019.
 */
@derive(encoder, decoder, order)
final case class PriceUI(
  id: PriceId,
  priceDateId: OfferDateId,
  dates: Ranges.Dates,
  nights: Nights,
  price: Amount,
  total: Amount
)

object PriceUI {
  type Id = PriceId

  implicit final val itemR: PriceUI => Id = _.id
}
