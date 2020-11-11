package bookingtour.protocols.excursions.values

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.DescriptionAPI
import bookingtour.protocols.excursions.newTypes.ExcursionProviderId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionTermProduct(
    id: ExcursionProviderId,
    clientTerms: List[DescriptionAPI],
    paymentTerms: List[DescriptionAPI],
    cancellationTerms: List[DescriptionAPI],
    taxTerms: List[DescriptionAPI]
)

object ExcursionTermProduct {
  final type Id = ExcursionProviderId

  implicit final val itemR: ExcursionTermProduct => Id = _.id

  implicit final val itemP: ExcursionTermProduct => Int = _ => 0
}
