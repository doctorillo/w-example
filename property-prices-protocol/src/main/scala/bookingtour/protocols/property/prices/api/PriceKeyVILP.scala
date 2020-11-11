package bookingtour.protocols.property.prices.api

import bookingtour.protocols.property.prices.newTypes.{ OfferDateId, PriceUnitId, TariffId }
import derevo.cats.order
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
 * © Alexey Toroshchin 2019.
 */
@derive(order)
final case class PriceKeyVILP(
  priceUnitId: PriceUnitId,
  priceDateId: OfferDateId,
  tariffId: TariffId
)
