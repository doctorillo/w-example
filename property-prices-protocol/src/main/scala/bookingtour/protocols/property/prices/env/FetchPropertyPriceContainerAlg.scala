package bookingtour.protocols.property.prices.env

import bookingtour.protocols.core.values.enumeration.PointItem
import bookingtour.protocols.parties.newTypes.{ PartyId, PointId }
import bookingtour.protocols.properties.newTypes.PropertyId
import bookingtour.protocols.property.prices.api.PropertyCardProductContainer
import zio.{ URIO, ZIO }

/**
 * © Alexey Toroshchin 2019.
 */
trait FetchPropertyPriceContainerAlg extends Serializable {
  val fetchPropertyPriceContainerAlg: FetchPropertyPriceContainerAlg.Service[Any]
}

object FetchPropertyPriceContainerAlg {
  trait Service[R] {
    def fetchOne(
      customerId: PartyId,
      propertyId: PropertyId
    ): URIO[R, Option[PropertyCardProductContainer]]

    def fetch(
      customerId: PartyId,
      pointId: PointId,
      pointCategory: PointItem
    ): URIO[R, Option[PropertyCardProductContainer]]
  }

  final object > extends Service[FetchPropertyPriceContainerAlg] {
    def fetchOne(
      customerId: PartyId,
      propertyId: PropertyId
    ): URIO[FetchPropertyPriceContainerAlg, Option[PropertyCardProductContainer]] =
      ZIO.accessM[FetchPropertyPriceContainerAlg](
        _.fetchPropertyPriceContainerAlg.fetchOne(
          customerId = customerId,
          propertyId = propertyId
        )
      )

    def fetch(
      customerId: PartyId,
      pointId: PointId,
      pointCategory: PointItem
    ): URIO[FetchPropertyPriceContainerAlg, Option[PropertyCardProductContainer]] =
      ZIO.accessM[FetchPropertyPriceContainerAlg](
        _.fetchPropertyPriceContainerAlg.fetch(
          customerId = customerId,
          pointId = pointId,
          pointCategory = pointCategory
        )
      )
  }
}
