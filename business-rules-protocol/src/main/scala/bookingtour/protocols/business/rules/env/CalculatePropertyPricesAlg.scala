package bookingtour.protocols.business.rules.env

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.{CurrencyItem, LangItem}
import bookingtour.protocols.parties.alg.RelationOrg.TerminateCustomerOrg
import bookingtour.protocols.parties.api.queries.QueryRoom
import bookingtour.protocols.parties.newTypes.{PartyId}
import bookingtour.protocols.property.prices.api.{PriceOp, PriceVariantUI, PropertyPriceCardProduct, StopSaleVILP}
import zio.URIO

/**
  * © Alexey Toroshchin 2019.
  */
trait CalculatePropertyPricesAlg extends Serializable {
  val calculatePropertyPricesAlg: CalculatePropertyPricesAlg.Service[Any]
}

object CalculatePropertyPricesAlg {
  trait Service[R] {
    def run(
        lang: LangItem,
        currency: CurrencyItem,
        dates: Ranges.Dates,
        groups: List[TerminateCustomerOrg],
        customer: PartyId,
        rooms: List[QueryRoom],
        card: PropertyPriceCardProduct,
        prices: List[PriceOp],
        stopSales: List[StopSaleVILP]
    ): URIO[R, List[PriceVariantUI]]
  }
}
