package bookingtour.protocols.interlook.source.prices

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.CurrencyItem
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.interlook.source.newTypes.{
  LookAccommodationId,
  LookBoardingId,
  LookCostId,
  LookCostTypeId,
  LookLinkServiceId,
  LookOfferId,
  LookPartyId,
  LookRoomCategoryId,
  LookRoomTypeId,
  LookSolverId,
  LookTariffId
}
import bookingtour.protocols.interlook.source.properties.{
  AccommodationKeyEP,
  BoardingKeyEP,
  RoomCategoryKeyEP,
  RoomTypeKeyEP
}
import cats.instances.all._
import cats.syntax.option._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CostAccommodationEP(
    id: LookCostId,
    property: LookPartyId,
    costType: LookCostTypeId,
    supplier: LookPartyId,
    typeRoom: LookRoomTypeId,
    boarding: LookBoardingId,
    categoryRoom: LookRoomCategoryId,
    accommodation: LookAccommodationId,
    offer: LookOfferId,
    linkService: LookLinkServiceId,
    tariff: LookTariffId,
    dates: Ranges.Dates,
    amount: Amount,
    solver: LookSolverId,
    stamp: Instant
)

object CostAccommodationEP {
  type Id = LookCostId

  final case class Output(
      id: Int,
      propertyId: Int,
      costType: Int,
      providerId: Int,
      typeId: Int,
      boardingId: Int,
      categoryId: Int,
      accommodationId: Int,
      offerId: Int,
      linkServiceId: Int,
      tariffId: Int,
      dateFrom: LocalDate,
      dateTo: LocalDate,
      amount: Double,
      currency: String,
      solverId: Int,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => CostAccommodationEP = _.into[CostAccommodationEP]
    .withFieldComputed(_.id, x => LookCostId(x.id))
    .withFieldComputed(_.property, x => LookPartyId(x.propertyId))
    .withFieldComputed(_.costType, x => LookCostTypeId(x.costType))
    .withFieldComputed(_.supplier, x => LookPartyId(x.providerId))
    .withFieldComputed(_.typeRoom, x => LookRoomTypeId(x.typeId))
    .withFieldComputed(_.boarding, x => LookBoardingId(x.boardingId))
    .withFieldComputed(_.categoryRoom, x => LookRoomCategoryId(x.categoryId))
    .withFieldComputed(_.accommodation, x => LookAccommodationId(x.accommodationId))
    .withFieldComputed(_.offer, x => LookOfferId(x.offerId))
    .withFieldComputed(_.linkService, x => LookLinkServiceId(x.linkServiceId))
    .withFieldComputed(_.tariff, x => LookTariffId(x.tariffId))
    .withFieldComputed(_.dates, x => Ranges.Dates(x.dateFrom, x.dateTo))
    .withFieldComputed(_.amount, x => Amount(x.amount, CurrencyItem.fromString(x.currency)))
    .withFieldComputed(_.solver, x => LookSolverId(x.solverId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  implicit final val itemR: CostAccommodationEP => Id = _.id

  implicit final val itemP0: CostAccommodationEP => LookPartyId = _.property

  final val itemP1: CostAccommodationEP => LookPartyId = _.supplier

  implicit final val item0: CostAccommodationEP => CostBaseEP =
    _.into[CostBaseEP].transform

  implicit final val item1: CostAccommodationEP => Option[CostBaseEP] =
    (x: CostAccommodationEP) => item0(x).some

  implicit final val item2: CostAccommodationEP => CostDatesEP =
    _.into[CostDatesEP].transform

  implicit final val item3: CostAccommodationEP => Option[CostDatesEP] =
    (x: CostAccommodationEP) => item2(x).some

  implicit final val item4: CostAccommodationEP => RoomTypeKeyEP =
    _.into[RoomTypeKeyEP].transform

  implicit final val item5: CostAccommodationEP => Option[RoomTypeKeyEP] =
    (x: CostAccommodationEP) => item4(x).some

  implicit final val item6: CostAccommodationEP => RoomCategoryKeyEP =
    _.into[RoomCategoryKeyEP].transform

  implicit final val item7: CostAccommodationEP => BoardingKeyEP =
    _.into[BoardingKeyEP].transform

  implicit final val item8: CostAccommodationEP => AccommodationKeyEP =
    _.into[AccommodationKeyEP].transform
}
