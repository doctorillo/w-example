package bookingtour.protocols.properties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.{DescriptionAPI, LabelAPI}
import bookingtour.protocols.properties.newTypes.{BoardingId, PropertyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyBoardingAPI(
    id: BoardingId,
    propertyId: PropertyId,
    category: BoardingVILP,
    labels: List[LabelAPI],
    descriptions: List[DescriptionAPI]
)

object PropertyBoardingAPI {
  type Id = BoardingId

  implicit final val itemR: PropertyBoardingAPI => Id = _.id

  implicit final val itemP: PropertyBoardingAPI => PropertyId = _.propertyId
}
