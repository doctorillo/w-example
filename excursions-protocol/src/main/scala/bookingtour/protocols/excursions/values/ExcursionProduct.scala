package bookingtour.protocols.excursions.values

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.Pax
import bookingtour.protocols.core.values.api.{DescriptionAPI, ImageAPI, LabelAPI}
import bookingtour.protocols.excursions.newTypes.ExcursionId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionProduct(
    id: ExcursionId,
    names: List[LabelAPI],
    descriptions: List[DescriptionAPI],
    images: List[ImageAPI],
    tags: List[ExcursionTagProduct],
    accommodationPax: Pax
)

object ExcursionProduct {
  final type Id = ExcursionId

  implicit final val itemR: ExcursionProduct => Id = _.id

  implicit final val itemP: ExcursionProduct => Int = _ => 0
}
