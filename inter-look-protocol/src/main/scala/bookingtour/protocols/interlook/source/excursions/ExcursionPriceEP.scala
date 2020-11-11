package bookingtour.protocols.interlook.source.excursions

import java.time.LocalDate

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.CurrencyItem
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.interlook.source.newTypes.{
  LookCostId,
  LookCustomerGroupId,
  LookExcursionId,
  LookOfferId,
  LookPartyId,
  LookPriceId,
  LookSolverId
}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionPriceEP(
    id: LookPriceId,
    solver: LookSolverId,
    cost: LookCostId,
    branch: LookPartyId,
    excursion: LookExcursionId,
    group: LookCustomerGroupId,
    offer: LookOfferId,
    dates: Ranges.Dates,
    price: Amount
)

object ExcursionPriceEP {
  final type Id = LookPriceId

  implicit final val itemR0: ExcursionPriceEP => Id = _.id

  implicit final val itemP0: ExcursionPriceEP => LookPartyId = _.branch

  final case class Output(
      id: Int,
      costId: Int,
      branchId: Int,
      excursionId: Int,
      groupId: Int,
      offerId: Int,
      dateFrom: LocalDate,
      dateTo: LocalDate,
      price: Double,
      currency: String,
      solverId: Int
  )

  implicit final val outputTransform: Output => ExcursionPriceEP = _.into[ExcursionPriceEP]
    .withFieldComputed(_.id, x => LookPriceId(x.id))
    .withFieldComputed(_.solver, x => LookSolverId(x.solverId))
    .withFieldComputed(_.cost, x => LookCostId(x.costId))
    .withFieldComputed(_.branch, x => LookPartyId(x.branchId))
    .withFieldComputed(_.excursion, x => LookExcursionId(x.excursionId))
    .withFieldComputed(_.group, x => LookCustomerGroupId(x.groupId))
    .withFieldComputed(_.offer, x => LookOfferId(x.offerId))
    .withFieldComputed(_.dates, x => Ranges.Dates(x.dateFrom, x.dateTo))
    .withFieldComputed(_.price, x => Amount(x.price, CurrencyItem.fromString(x.currency)))
    .transform
}
