package bookingtour.protocols.interlook.source.prices

import java.time.Instant

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.{CurrencyItem, GoodsCategoryItem}
import bookingtour.protocols.interlook.source.newTypes.{
  LookCostId,
  LookCostTypeId,
  LookOfferId,
  LookPartyId,
  LookSolverId
}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CostEP(
    id: LookCostId,
    code: Int,
    costType: LookCostTypeId,
    provider: LookPartyId,
    category: GoodsCategoryItem,
    code1: Option[Int],
    code2: Option[Int],
    code3: Option[Int],
    acCode: Option[Int],
    spo: Option[LookOfferId],
    dates: Ranges.Dates,
    amount: Double,
    currency: CurrencyItem,
    solver: LookSolverId,
    updated: Instant
)

object CostEP {
  type Id = LookCostId
  implicit final val itemR: CostEP => Id = _.id

  implicit final val itemP: CostEP => Option[LookOfferId] = _.spo
}
