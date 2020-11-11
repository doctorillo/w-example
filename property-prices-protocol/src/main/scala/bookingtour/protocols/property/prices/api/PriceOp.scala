package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.Amount
import bookingtour.protocols.interlook.source.newTypes._
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PartyId}
import bookingtour.protocols.properties.newTypes.{BoardingId, PropertyId}
import bookingtour.protocols.property.prices.newTypes.{OfferDateId, PriceId, PriceUnitId, TariffId, VariantId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PriceOp(
    id: PriceId,
    priceSync: LookPriceId,
    property: PropertyId,
    propertySync: LookPartyId,
    supplier: PartyId,
    supplierSync: LookPartyId,
    tariff: TariffId,
    group: CustomerGroupId,
    offerDate: OfferDateId,
    priceUnit: PriceUnitId,
    variant: VariantId,
    boarding: BoardingId,
    amount: Amount
)

object PriceOp {
  final type Id = PriceId

  @derive(encoder, decoder, order)
  final case class PricePartition(
      group: CustomerGroupId,
      variant: VariantId
  )

  final val partitionKey: PriceOp => PricePartition = _.into[PricePartition].transform

  implicit final val itemR0: PriceOp => Id = _.id

  implicit final val itemP0: PriceOp => PropertyId = _.property

  implicit final val itemP1: PriceOp => PricePartition = x => partitionKey(x)
}
