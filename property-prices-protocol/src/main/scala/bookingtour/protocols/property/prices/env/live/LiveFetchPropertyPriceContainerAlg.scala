package bookingtour.protocols.property.prices.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.values.enumeration.{ContextItem, PointItem}
import bookingtour.protocols.parties.env.RelationCustomerAlg
import bookingtour.protocols.parties.newTypes.{CityId, PartyId, PointId}
import bookingtour.protocols.properties.newTypes.PropertyId
import bookingtour.protocols.property.prices.api.{PropertyCardProductContainer, PropertyPriceCardProduct}
import bookingtour.protocols.property.prices.env.{FetchPropertyPriceContainerAlg, PropertyPointAlg}
import cats.instances.all._
import cats.syntax.option._
import cats.syntax.order._
import zio.{URIO, ZIO}
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveFetchPropertyPriceContainerAlg private (
    relationAlg: RelationCustomerAlg.Service[Any],
    propertyPointAlg: PropertyPointAlg.Service[Any],
    propertyCardProductConsumer: ConsumerAlg.Aux[Any, CityId, PropertyPriceCardProduct]
) extends FetchPropertyPriceContainerAlg {
  val fetchPropertyPriceContainerAlg: FetchPropertyPriceContainerAlg.Service[Any] =
    new FetchPropertyPriceContainerAlg.Service[Any] {
      def fetchOne(
          customerId: PartyId,
          propertyId: PropertyId
      ): URIO[Any, Option[PropertyCardProductContainer]] =
        for {
          b <- relationAlg.makeInit(id = customerId, context = ContextItem.Accommodation)
          c <- relationAlg.fetchTerminate(b)
          d <- propertyCardProductConsumer
                .byValue(x => x.id === propertyId && c.exists(_.partyId.x === x.supplier.x))
                .catchAll(_ => ZIO.succeed(List.empty))
          e = if (d.nonEmpty) {
            PropertyCardProductContainer(customer = b, properties = d).some
          } else {
            none[PropertyCardProductContainer]
          }
        } yield e

      def fetch(
          customerId: PartyId,
          pointId: PointId,
          pointCategory: PointItem
      ): URIO[Any, Option[PropertyCardProductContainer]] =
        for {
          a <- propertyPointAlg.fetchCities(pointId, pointCategory)
          b <- relationAlg.makeInit(id = customerId, context = ContextItem.Accommodation)
          c <- relationAlg.fetchTerminate(b)
          d <- propertyCardProductConsumer
                .byKeyValue(a.contains(_), x => c.exists(_.partyId.x === x.supplier.x))
                .catchAll(_ => ZIO.succeed(List.empty))
          e = if (d.nonEmpty) {
            PropertyCardProductContainer(customer = b, properties = d).some
          } else {
            none[PropertyCardProductContainer]
          }
        } yield e
    }
}

object LiveFetchPropertyPriceContainerAlg {
  final def apply()(
      implicit relationAlg: RelationCustomerAlg,
      propertyPointAlg: PropertyPointAlg.Service[Any],
      propertyCardProductConsumer: ConsumerAlg.Aux[Any, CityId, PropertyPriceCardProduct]
  ): FetchPropertyPriceContainerAlg =
    new LiveFetchPropertyPriceContainerAlg(
      relationAlg = relationAlg.relationCustomerAlg,
      propertyPointAlg = propertyPointAlg,
      propertyCardProductConsumer = propertyCardProductConsumer
    )
}
