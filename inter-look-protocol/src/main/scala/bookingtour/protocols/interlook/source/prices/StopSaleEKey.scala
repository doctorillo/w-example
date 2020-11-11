package bookingtour.protocols.interlook.source.prices

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Month, Year}
import bookingtour.protocols.interlook.source.newTypes.{LookPartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order)
final case class StopSaleEKey(property: LookPartyId, year: Year, month: Month)
