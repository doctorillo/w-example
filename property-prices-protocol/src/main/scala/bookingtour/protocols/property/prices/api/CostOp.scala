package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.Amount
import bookingtour.protocols.interlook.source.newTypes._
import bookingtour.protocols.parties.newTypes.PartyId
import bookingtour.protocols.properties.newTypes.{BoardingId, PropertyId}
import bookingtour.protocols.property.prices.newTypes.{CostId, OfferDateId, PriceUnitId, TariffId, VariantId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class CostOp(
    id: CostId,
    costSync: LookCostId,
    property: PropertyId,
    propertySync: LookPartyId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    tariff: TariffId,
    offerDate: OfferDateId,
    priceUnit: PriceUnitId,
    variant: VariantId,
    boarding: BoardingId,
    amount: Amount
)

object CostOp {
  final type Id = CostId

  implicit final val itemR0: CostOp => Id = _.id

  implicit final val itemP0: CostOp => PropertyId = _.property
}
