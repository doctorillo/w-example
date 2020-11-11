package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.{ Counter, Nights, Pax }
import bookingtour.protocols.core.newtypes.values.{
  BoardingLabel,
  RoomCategoryLabel,
  RoomTypeLabel,
  TariffLabel
}
import bookingtour.protocols.core.values.Amount
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.property.prices.newTypes.PriceUnitId
import derevo.cats.order
import derevo.circe.{ decoder, encoder }
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
 * © Alexey Toroshchin 2019.
 */
@derive(encoder, decoder, order)
final case class BestPriceUI(
  priceUnitId: PriceUnitId,
  lang: LangItem,
  roomType: RoomTypeLabel,
  roomCategory: RoomCategoryLabel,
  boarding: BoardingLabel,
  tariff: TariffLabel,
  nights: Nights,
  pax: Pax,
  stopSale: Boolean,
  resultCount: Counter,
  price: Amount,
  discount: Option[Amount],
  total: Amount
)

object BestPriceUI {
  type Id = PriceUnitId

  implicit final val itemR: BestPriceUI => Id = _.priceUnitId
}
