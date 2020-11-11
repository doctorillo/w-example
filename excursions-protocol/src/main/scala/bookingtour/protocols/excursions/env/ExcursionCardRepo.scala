package bookingtour.protocols.excursions.env

import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.{CurrencyItem, LangItem, PointItem}
import bookingtour.protocols.excursions.api.ExcursionCardUI
import bookingtour.protocols.excursions.api.ExcursionCardUI
import bookingtour.protocols.parties.newTypes.{PartyId, PointId}
import zio.{URIO, ZIO}

/**
  * © Alexey Toroshchin 2020.
  */
trait ExcursionCardRepo extends Serializable {
  val excursionCardRepo: ExcursionCardRepo.Service[Any]
}

object ExcursionCardRepo {
  trait Service[R] {

    def fetch(
        lang: LangItem,
        customerId: PartyId,
        pointId: PointId,
        pointCategory: PointItem,
        dates: Ranges.Dates,
        currency: CurrencyItem
    ): URIO[R, List[ExcursionCardUI]]

  }

  final object > extends Service[ExcursionCardRepo] {
    def fetch(
        lang: LangItem,
        customerId: PartyId,
        pointId: PointId,
        pointCategory: PointItem,
        dates: Ranges.Dates,
        currency: CurrencyItem
    ): URIO[ExcursionCardRepo, List[ExcursionCardUI]] =
      ZIO.accessM(
        _.excursionCardRepo.fetch(lang, customerId, pointId, pointCategory, dates, currency)
      )
  }
}
