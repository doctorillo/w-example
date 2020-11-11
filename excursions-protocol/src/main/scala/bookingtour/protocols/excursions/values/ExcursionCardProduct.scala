package bookingtour.protocols.excursions.values

import bookingtour.protocols.core.values.api.{DescriptionAPI, ImageAPI, LabelAPI}
import bookingtour.protocols.core.values.enumeration.{CurrencyItem, LangItem, PointItem}
import bookingtour.protocols.excursions.api.ExcursionCardUI
import bookingtour.protocols.excursions.newTypes.{ExcursionId, ExcursionProviderId}
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.Pax
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.parties.api.PointUI
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, PointId}
import cats.syntax.order._
import bookingtour.protocols.core.types.FunctionKCore.instances._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionCardProduct(
    id: ExcursionId,
    providerId: ExcursionProviderId,
    names: List[LabelAPI],
    descriptions: List[DescriptionAPI],
    images: List[ImageAPI],
    tags: List[ExcursionTagProduct],
    clientTerms: List[DescriptionAPI],
    paymentTerms: List[DescriptionAPI],
    cancellationTerms: List[DescriptionAPI],
    taxTerms: List[DescriptionAPI],
    accommodationPax: Pax,
    attached: List[ExcursionOfferProduct],
    prices: List[ExcursionPriceProduct]
)

object ExcursionCardProduct {
  final type Id = ExcursionId

  implicit final val itemR: ExcursionCardProduct => Id = _.id

  implicit final val itemP: ExcursionCardProduct => Int = _ => 0

  final val toApi
      : (ExcursionCardProduct, Ranges.Dates, CustomerGroupId, CurrencyItem, LangItem) => Option[ExcursionCardUI] =
    (card, dates, groupId, currency, lang) => {
      for {
        name              <- card.names.find(_.lang === lang).map(_.label)
        description       <- card.descriptions.find(_.lang === lang).map(_.data)
        clientTerms       = card.clientTerms.find(_.lang === lang).map(_.data).getOrElse("")
        paymentTerms      = card.paymentTerms.find(_.lang === lang).map(_.data).getOrElse("")
        cancellationTerms = card.cancellationTerms.find(_.lang === lang).map(_.data).getOrElse("")
        taxTerms          = card.taxTerms.find(_.lang === lang).map(_.data).getOrElse("")
        images            = card.images.filter(_.label.lang === lang)
        offer             <- card.attached.find(x => x.dates.intersected(dates))
        excursionDates = offer.dates
          .intersect(dates)
          .map(_.localDate[List])
          .getOrElse(List.empty)
          .filter(x => offer.days.exists(_.x === x.getDayOfWeek.getValue))
        point <- offer.pointNames.find(_.lang === lang)
        price <- card.prices.find(x => x.groupId === groupId && x.amount.currency === currency).map(_.amount)
        pickupPoint = PointUI(
          id = PointId(offer.pickupPoint.x),
          parent = Some(PointId(offer.city.x)),
          label = point,
          category = PointItem.Collector
        )
        if excursionDates.nonEmpty
      } yield ExcursionCardUI(
        id = offer.id,
        excursionId = offer.excursion,
        lang = lang,
        description = description,
        clientTerms = clientTerms,
        paymentTerms = paymentTerms,
        cancellationTerms = cancellationTerms,
        taxTerms = taxTerms,
        name = name,
        images = images,
        tags = card.tags.map(_.value).distinct,
        pickupPoint = pickupPoint,
        dates = excursionDates,
        accommodationPax = card.accommodationPax,
        startTime = offer.startTime,
        duration = offer.duration,
        days = offer.days,
        age = Ranges.Ints(0, 100),
        price = price
      )
    }

  implicit final class ExcursionCardProductOps(private val self: ExcursionCardProduct) {
    def toUI(
        dates: Ranges.Dates,
        group: CustomerGroupId,
        currency: CurrencyItem,
        lang: LangItem
    ): Option[ExcursionCardUI] = toApi(self, dates, group, currency, lang)
  }
}
