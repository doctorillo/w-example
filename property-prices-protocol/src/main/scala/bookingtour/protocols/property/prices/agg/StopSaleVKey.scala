package bookingtour.protocols.property.prices.agg

import derevo.cats.order
import derevo.derive
import bookingtour.protocols.core.newtypes.quantities.{ Month, Year }
import bookingtour.protocols.properties.newTypes.PropertyId
import cats.instances.all._
import bookingtour.protocols.core._

/**
 * © Alexey Toroshchin 2019.
 */
@derive(order)
final case class StopSaleVKey(propertyId: PropertyId, month: Month, year: Year)
