package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.{Nights, Position}
import bookingtour.protocols.core.newtypes.values._
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.parties.api.queries.QueryGuest
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId}
import bookingtour.protocols.properties.newTypes.BoardingId
import bookingtour.protocols.property.prices.newTypes.{PriceUnitId, TariffId, VariantId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PriceUnitUI(
    id: PriceUnitId,
    lang: LangItem,
    customerId: PartyId,
    groupId: CustomerGroupId,
    roomPosition: List[Position],
    guests: List[QueryGuest],
    checkInDates: Ranges.Dates,
    tariffId: TariffId,
    tariffLabel: TariffLabel,
    variantId: VariantId,
    boardingId: BoardingId,
    boardingLabel: BoardingLabel,
    roomTypeLabel: RoomTypeLabel,
    roomCategoryLabel: RoomCategoryLabel,
    prices: List[PriceUI],
    nights: Nights,
    price: Amount,
    discount: Option[Amount],
    total: Amount,
    description: Option[PropertyDescription],
    stopSale: Boolean
)

object PriceUnitUI {
  type Id = PriceUnitId

  implicit final val itemR: PriceUnitUI => Id = _.id

  implicit final val itemP: PriceUnitUI => CustomerGroupId = _.groupId

  final val byPriceOrdering: Ordering[PriceUnitUI] = (x: PriceUnitUI, y: PriceUnitUI) =>
    x.total.value.compareTo(y.total.value)
}
