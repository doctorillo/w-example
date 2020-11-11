package bookingtour.protocols.property.prices.env

import bookingtour.protocols.core.newtypes.quantities.Position
import bookingtour.protocols.parties.api.queries.QueryGroup
import bookingtour.protocols.property.prices.api.{
  PropertyPriceCardProduct,
  RoomVariantVILP,
  VariantOp
}
import zio.{ URIO, ZIO }

/**
 * © Alexey Toroshchin 2019.
 */
trait RoomVariantAlg extends Serializable {
  val roomVariantAlg: RoomVariantAlg.Service[Any]
}

object RoomVariantAlg {
  final type V = (Position, PropertyPriceCardProduct, List[VariantOp])

  trait Service[R] {
    def fetch(
      query: QueryGroup,
      properties: List[PropertyPriceCardProduct]
    ): URIO[R, List[V]]
  }

  final object > extends Service[RoomVariantAlg] {
    def fetch(
      query: QueryGroup,
      properties: List[PropertyPriceCardProduct]
    ): URIO[RoomVariantAlg, List[V]] =
      ZIO.accessM(_.roomVariantAlg.fetch(query, properties))
  }
}
