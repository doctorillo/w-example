package bookingtour.protocols.excursions.api

import java.time.{LocalDate, LocalTime}

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.{Hour, Pax, WeekDay}
import bookingtour.protocols.core.values.api.ImageAPI
import bookingtour.protocols.core.values.enumeration.{ExcursionTagItem, LangItem}
import bookingtour.protocols.core.values.{Amount, Ranges}
import bookingtour.protocols.excursions.newTypes.{ExcursionId, ExcursionOfferId}
import bookingtour.protocols.parties.api.PointUI
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionCardUI(
    id: ExcursionOfferId,
    excursionId: ExcursionId,
    lang: LangItem,
    name: String,
    description: String,
    clientTerms: String,
    paymentTerms: String,
    cancellationTerms: String,
    taxTerms: String,
    images: List[ImageAPI],
    tags: List[ExcursionTagItem],
    pickupPoint: PointUI,
    dates: List[LocalDate],
    accommodationPax: Pax,
    startTime: LocalTime,
    duration: Hour,
    days: List[WeekDay],
    age: Ranges.Ints,
    price: Amount
)

object ExcursionCardUI {
  final type Id = ExcursionOfferId

  implicit final val itemR: ExcursionCardUI => Id = _.id

  implicit final val itemP: ExcursionCardUI => Int = _ => 0
}
