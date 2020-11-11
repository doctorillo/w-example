package bookingtour.protocols.orders.api

import java.time.LocalDateTime

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Nights, PropertyStar}
import bookingtour.protocols.core.newtypes.values.{BoardingLabel, RoomCategoryLabel, RoomTypeLabel, TariffLabel}
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.orders.newTypes.PlannerAccommodationItemId
import bookingtour.protocols.parties.newTypes.CustomerGroupId
import bookingtour.protocols.properties.newTypes.PropertyId
import bookingtour.protocols.property.prices.newTypes.{PriceUnitId, TariffId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerAccommodationItem(
    id: PlannerAccommodationItemId,
    created: LocalDateTime,
    updated: LocalDateTime,
    dates: Ranges.Dates,
    propertyId: PropertyId,
    propertyName: String,
    propertyStar: PropertyStar,
    roomPriceUnitId: PriceUnitId,
    roomType: RoomTypeLabel,
    roomCategory: RoomCategoryLabel,
    boarding: BoardingLabel,
    tariffId: TariffId,
    tariff: TariffLabel,
    groupId: CustomerGroupId,
    nights: Nights,
    price: Amount,
    discount: Option[Amount],
    markAsBest: Boolean,
    markAsDelete: Boolean
)
