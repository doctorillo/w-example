package bookingtour.protocols.properties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.properties.newTypes.CategoryBoardingId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BoardingVILP(
    id: CategoryBoardingId,
    code: String,
    labels: List[LabelAPI],
    treatment: Boolean
)

object BoardingVILP {
  type Id = CategoryBoardingId

  implicit final val itemR0: BoardingVILP => Id = _.id

  implicit final val itemP0: BoardingVILP => Int = _ => 0
}
