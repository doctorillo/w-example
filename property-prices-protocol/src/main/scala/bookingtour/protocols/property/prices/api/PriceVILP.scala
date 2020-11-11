package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.values.Amount
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.newTypes.CustomerGroupId
import bookingtour.protocols.property.prices.newTypes._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PriceVILP(
    id: PriceId,
    propertyProviderId: PropertyProviderId,
    priceUnitId: PriceUnitId,
    tariffId: TariffId,
    priceDateId: OfferDateId,
    groupId: CustomerGroupId,
    syncs: List[SyncItem],
    price: Amount
)

object PriceVILP {
  type Id = PriceId

  implicit final val itemR: PriceVILP => Id = _.id

  implicit final val itemP: PriceVILP => PropertyProviderId = _.propertyProviderId

  implicit final val itemP0: PriceVILP => PriceKeyVILP = x =>
    PriceKeyVILP(
      x.priceUnitId,
      x.priceDateId,
      x.tariffId
    )
}
