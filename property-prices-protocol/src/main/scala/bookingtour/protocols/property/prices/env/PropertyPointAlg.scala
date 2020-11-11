package bookingtour.protocols.property.prices.env

import bookingtour.protocols.core.values.enumeration.{ LangItem, PointItem }
import bookingtour.protocols.parties.api.PointUI
import bookingtour.protocols.parties.newTypes.{ CityId, PointId }
import zio.{ URIO, ZIO }

/**
 * © Alexey Toroshchin 2019.
 */
trait PropertyPointAlg extends Serializable {
  val propertyPointAlg: PropertyPointAlg.Service[Any]
}

object PropertyPointAlg {
  trait Service[R] {
    def fetchAll(lang: LangItem): URIO[R, List[PointUI]]

    def fetchCities(
      pointId: PointId,
      pointType: PointItem
    ): URIO[R, List[CityId]]
  }

  final object > {
    def fetchAll(lang: LangItem): URIO[PropertyPointAlg, List[PointUI]] =
      ZIO.accessM(_.propertyPointAlg.fetchAll(lang))

    def fetchCities(
      pointId: PointId,
      pointType: PointItem
    ): URIO[PropertyPointAlg, List[CityId]] =
      ZIO.accessM(_.propertyPointAlg.fetchCities(pointId, pointType))
  }
}
