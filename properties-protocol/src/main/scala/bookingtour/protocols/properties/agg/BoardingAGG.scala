package bookingtour.protocols.properties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.LabelE
import bookingtour.protocols.properties.newTypes.CategoryBoardingId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BoardingAGG(
    id: CategoryBoardingId,
    code: String,
    withTreatment: Boolean,
    labels: List[LabelE]
)

object BoardingAGG {
  type Id = CategoryBoardingId

  implicit final val itemR: BoardingAGG => Id = _.id

  implicit final val itemP: BoardingAGG => Int = _ => 0
}
