package bookingtour.protocols.interlook.source.prices

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.interlook.source.newTypes.{LookLinkServiceId, LookOfferId, LookPartyId, LookTariffId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CostDatesEP(
    property: LookPartyId,
    supplier: LookPartyId,
    offer: LookOfferId,
    linkService: LookLinkServiceId,
    tariff: LookTariffId,
    dates: Ranges.Dates
)

object CostDatesEP {
  type Id = CostDatesEP

  implicit final val itemR: CostDatesEP => Id = x => x

  implicit final val itemP: CostDatesEP => LookPartyId = _.property
}
