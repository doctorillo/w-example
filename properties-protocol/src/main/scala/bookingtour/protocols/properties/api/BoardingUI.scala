package bookingtour.protocols.properties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.properties.newTypes.CategoryBoardingId
import bookingtour.protocols.properties.newTypes.CategoryBoardingId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{ decoder, encoder }
import derevo.derive

/**
 * © Alexey Toroshchin 2019.
 */
@derive(encoder, decoder, order)
final case class BoardingUI(
  id: CategoryBoardingId,
  code: String,
  withTreatment: Boolean,
  label: LabelAPI
)

object BoardingUI {
  type Id = CategoryBoardingId

  implicit final val itemR: BoardingUI => Id = _.id

  implicit final val itemP: BoardingUI => (
    Int,
    BoardingUI
  ) = x => (0, x)
}
