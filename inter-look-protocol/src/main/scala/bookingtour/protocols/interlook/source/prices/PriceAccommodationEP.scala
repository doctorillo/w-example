package bookingtour.protocols.interlook.source.prices

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core._
import bookingtour.protocols.core.types.CompareOps
import bookingtour.protocols.core.values.enumeration.CurrencyItem
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.interlook.source.newTypes.{
  LookCostId,
  LookCustomerGroupId,
  LookOfferId,
  LookPartyId,
  LookPriceId,
  LookSolverId,
  LookTariffId
}
import cats.Order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder)
final case class PriceAccommodationEP(
    id: LookPriceId,
    property: LookPartyId,
    provider: LookPartyId,
    group: LookCustomerGroupId,
    cost: LookCostId,
    offer: LookOfferId,
    tariff: LookTariffId,
    dates: Ranges.Dates,
    amount: Amount,
    solver: LookSolverId,
    stamp: Instant
)

object PriceAccommodationEP {
  type Id = LookPriceId

  // TODO |ZZZ|
  implicit final val itemO: Order[PriceAccommodationEP] =
    (x: PriceAccommodationEP, y: PriceAccommodationEP) => CompareOps.lazyCompareOps(() => x.id.x.compareTo(y.id.x))

  implicit final val itemR: PriceAccommodationEP => Id = _.id

  implicit final val itemP0: PriceAccommodationEP => LookPartyId = _.property

  final case class Output(
      id: Int,
      propertyId: Int,
      providerId: Int,
      groupId: Int,
      costId: Int,
      offerId: Int,
      tariffId: Int,
      dateFrom: LocalDate,
      dateTo: LocalDate,
      amount: Double,
      currency: String,
      solverId: Int,
      updated: LocalDateTime
  )

  implicit final val outputTransform: Output => PriceAccommodationEP = _.into[PriceAccommodationEP]
    .withFieldComputed(_.id, x => LookPriceId(x.id))
    .withFieldComputed(_.property, x => LookPartyId(x.propertyId))
    .withFieldComputed(_.provider, x => LookPartyId(x.providerId))
    .withFieldComputed(_.group, x => LookCustomerGroupId(x.groupId))
    .withFieldComputed(_.cost, x => LookCostId(x.costId))
    .withFieldComputed(_.offer, x => LookOfferId(x.offerId))
    .withFieldComputed(_.tariff, x => LookTariffId(x.tariffId))
    .withFieldComputed(_.dates, x => Ranges.Dates(x.dateFrom, x.dateTo))
    .withFieldComputed(_.amount, x => Amount(x.amount, CurrencyItem.fromString(x.currency)))
    .withFieldComputed(_.solver, x => LookSolverId(x.solverId))
    .withFieldComputed(_.stamp, _.updated.toInstant(ZoneOffset.UTC))
    .transform
}
