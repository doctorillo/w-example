package bookingtour.protocols.excursions.values

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.core.values.enumeration.ExcursionTagItem
import bookingtour.protocols.excursions.newTypes.{ExcursionId, ExcursionTagId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionTagProduct(
    id: ExcursionTagId,
    excursionId: ExcursionId,
    value: ExcursionTagItem,
    labels: List[LabelAPI]
)

object ExcursionTagProduct {
  final type Id = ExcursionTagId

  implicit final val itemR: ExcursionTagProduct => Id = _.id

  implicit final val itemP: ExcursionTagProduct => Int = _ => 0
}
