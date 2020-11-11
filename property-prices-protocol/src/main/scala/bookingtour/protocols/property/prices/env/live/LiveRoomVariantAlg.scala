package bookingtour.protocols.property.prices.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.protocols.core.newtypes.quantities.Age
import bookingtour.protocols.parties.api.queries.{QueryGroup, QueryGuest}
import bookingtour.protocols.property.prices.api.{
  AccommodationGuestOp,
  AccommodationVILP,
  PropertyPriceCardProduct,
  RoomVariantVILP
}
import bookingtour.protocols.property.prices.env.{RoomVariantAlg}
import bookingtour.protocols.property.prices.newTypes.PropertyProviderId
import cats.syntax.order._
import zio.ZIO
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.CityId

/**
  * © Alexey Toroshchin 2019.
  */
final class LiveRoomVariantAlg private extends RoomVariantAlg {

  private def check(x: List[AccommodationGuestOp], y: List[QueryGuest]): Boolean = {
    if (x.length =!= y.length) {
      false
    } else {
      x.foldLeft((true, y)) { (acc, x) =>
          if (!acc._1) {
            acc
          } else {
            acc._2.find(z => x.age.contains(z.age.getOrElse(Age.Adult))) match {
              case None =>
                (false, List.empty)

              case Some(value) =>
                (true, acc._2.filterNot(_ === value))
            }
          }
        }
        ._1
    }
  }

  val roomVariantAlg: RoomVariantAlg.Service[Any] =
    (query: QueryGroup, properties: List[PropertyPriceCardProduct]) => {
      val rooms = query.rooms.map(x => (x.position, x.guests))
      val variants = for {
        (position, guests) <- rooms
        property           <- properties
        variants = property.variants.filter(x =>
          x.propertyProvider === property.propertyProvider && check(x.accommodation.guests, guests)
        )
        if variants.nonEmpty
      } yield (position, property, variants)
      ZIO.succeed(variants)
    }
}

object LiveRoomVariantAlg {
  final def apply(): RoomVariantAlg = new LiveRoomVariantAlg
}
