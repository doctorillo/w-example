package bookingtour.protocols.api.business.business

import java.time.LocalDateTime
import java.util.UUID

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.properties.newTypes.PropertyId

/**
  * © Alexey Toroshchin 2019.
  */
sealed abstract class ContextRule extends Product with Serializable

object ContextRule {
  final case class PropertyContextRule(
      created: LocalDateTime,
      propertyId: PropertyId,
      star: Int,
      pointId: UUID,
      dates: Ranges.Dates /*,
    clients: List[GuestPRC],
    prices: List[PriceChunkUI]*/
  ) extends ContextRule
}
