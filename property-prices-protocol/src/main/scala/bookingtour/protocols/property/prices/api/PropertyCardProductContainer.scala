package bookingtour.protocols.property.prices.api

import bookingtour.protocols.parties.alg.RelationOrg.InitCustomerOrg
import derevo.cats.order
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.parties.api.queries.QueryGroup
import cats.syntax.order._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order)
final case class PropertyCardProductContainer(
    customer: InitCustomerOrg,
    properties: List[PropertyPriceCardProduct]
)

object PropertyCardProductContainer {
  implicit final class PropertyCardProductContainerOps(private val self: PropertyCardProductContainer) {
    def filter(dates: Ranges.Dates, query: QueryGroup): PropertyCardProductContainer = {
      val properties = for {
        card       <- self.properties
        offerDates = card.offerDates.filter(_.dates.intersected(dates))
        variants   = card.variants.filter(x => query.rooms.exists(z => x.accommodation.condition(z.guests)))
        roomUnits  = card.roomUnits.filter(x => variants.exists(_.roomUnit === x.id))
        priceUnits = card.priceUnits.filter(x => variants.exists(_.id === x.variant))
        if offerDates.nonEmpty && priceUnits.nonEmpty
      } yield card.copy(offerDates = offerDates, variants = variants, roomUnits = roomUnits, priceUnits = priceUnits)
      self.copy(properties = properties)
    }
  }
}
