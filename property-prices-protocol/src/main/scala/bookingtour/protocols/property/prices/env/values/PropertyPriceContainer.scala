package bookingtour.protocols.property.prices.env.values

import bookingtour.protocols.parties.api.queries.QueryGuest
import bookingtour.protocols.properties.api.{ BoardingProduct, PropertyCardProduct }
import bookingtour.protocols.property.prices.api._

/**
 * © Alexey Toroshchin 2019.
 */
final case class PropertyPriceContainer(
  property: PropertyCardProduct,
  boardings: List[BoardingProduct],
  units: List[PriceUnitVILP],
  tariffs: List[TariffVILP],
  variants: List[(QueryGuest, RoomVariantVILP)],
  prices: List[PriceVILP],
  stops: List[StopSaleVILP]
)
