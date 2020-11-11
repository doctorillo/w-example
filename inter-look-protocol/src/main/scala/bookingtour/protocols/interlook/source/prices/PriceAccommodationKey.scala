package bookingtour.protocols.interlook.source.prices

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookOfferId, LookPartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order)
final case class PriceAccommodationKey(
    property: LookPartyId,
    group: LookCustomerGroupId,
    offer: LookOfferId
)
