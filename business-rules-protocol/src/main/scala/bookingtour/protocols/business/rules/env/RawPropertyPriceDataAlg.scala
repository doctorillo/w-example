package bookingtour.protocols.business.rules.env

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.parties.api.queries.QueryGroup
import bookingtour.protocols.property.prices.api.{
  PriceVariantUI,
  PropertyCardProductContainer,
  PropertyPriceCardProduct
}
import zio.{URIO, ZIO}

/**
  * © Alexey Toroshchin 2019.
  */
trait RawPropertyPriceDataAlg extends Serializable {
  val rawPropertyPriceDataAlg: RawPropertyPriceDataAlg.Service[Any]
}

object RawPropertyPriceDataAlg {
  trait Service[R] {
    def calculate(
        lang: LangItem,
        query: QueryGroup,
        dates: Ranges.Dates,
        cards: PropertyCardProductContainer
    ): URIO[R, Map[PropertyPriceCardProduct, List[PriceVariantUI]]]
  }

  final object > extends Service[RawPropertyPriceDataAlg] {
    def calculate(
        lang: LangItem,
        query: QueryGroup,
        dates: Ranges.Dates,
        cards: PropertyCardProductContainer
    ): URIO[RawPropertyPriceDataAlg, Map[PropertyPriceCardProduct, List[PriceVariantUI]]] =
      ZIO.accessM(_.rawPropertyPriceDataAlg.calculate(lang, query, dates, cards))
  }
}
