package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.{Counter, Nights, Pax, Position}
import bookingtour.protocols.core.newtypes.values.{BoardingLabel, RoomCategoryLabel, RoomTypeLabel, TariffLabel}
import bookingtour.protocols.core.values.enumeration.{CurrencyItem, LangItem}
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.parties.api.queries.QueryGuest
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId}
import bookingtour.protocols.properties.newTypes.{BoardingId, PropertyId, RoomCategoryId, RoomTypeId}
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
final case class PriceVariantUI(
    lang: LangItem,
    currency: CurrencyItem,
    propertyId: PropertyId,
    customerId: PartyId,
    supplierId: PartyId,
    groupId: CustomerGroupId,
    checkInDates: Ranges.Dates,
    roomOrder: Position,
    guests: List[QueryGuest],
    priceUnitId: PriceUnitId,
    variantId: VariantId,
    roomTypeId: RoomTypeId,
    roomType: RoomTypeLabel,
    roomCategoryId: RoomCategoryId,
    roomCategory: RoomCategoryLabel,
    boardingId: BoardingId,
    boarding: BoardingLabel,
    tariffId: TariffId,
    tariff: TariffLabel,
    nights: Nights,
    pax: Pax,
    stopSale: Boolean,
    resultCount: Counter,
    chunks: List[PriceChunkUI],
    discount: Option[Amount],
    amount: Amount,
    total: Amount
)

object PriceVariantUI {
  type Id = PriceUnitId

  implicit final val itemR: PriceVariantUI => Id = _.priceUnitId

  implicit final val itemP: PriceVariantUI => CustomerGroupId = _.groupId

  implicit final class PriceVariantUiOps(private val self: PriceVariantUI) extends AnyVal {
    def toPriceUnit: PriceUnitUI = PriceUnitUI(
      id = self.priceUnitId,
      lang = self.lang,
      customerId = self.customerId,
      groupId = self.groupId,
      roomPosition = List(self.roomOrder),
      guests = self.guests,
      checkInDates = self.checkInDates,
      tariffId = self.tariffId,
      tariffLabel = self.tariff,
      variantId = self.variantId,
      boardingId = self.boardingId,
      boardingLabel = self.boarding,
      roomTypeLabel = self.roomType,
      roomCategoryLabel = self.roomCategory,
      prices = self.chunks.map(x =>
        PriceUI(
          id = x.id,
          priceDateId = x.priceDateId,
          dates = x.dates,
          nights = x.nights,
          price = x.amount,
          total = x.price
        )
      ),
      nights = self.nights,
      price = self.amount,
      discount = self.discount,
      total = self.total,
      description = None,
      stopSale = self.stopSale
    )
  }
}
