package bookingtour.protocols.properties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.{DescriptionAPI, LabelAPI}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.properties.newTypes.{BoardingId, CategoryBoardingId, PropertyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.{loggable}

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order, loggable)
final case class BoardingProduct(
    id: BoardingId,
    property: PropertyId,
    category: CategoryBoardingId,
    categoryCode: String,
    names: List[LabelAPI],
    descriptions: List[DescriptionAPI],
    syncs: List[SyncItem],
    treatment: Boolean
)

object BoardingProduct {
  type Id = BoardingId

  implicit final val itemR0: BoardingProduct => Id = _.id

  implicit final val itemP0: BoardingProduct => PropertyId = _.property
}
