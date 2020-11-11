package bookingtour.protocols.properties.api

import java.util.UUID

import bookingtour.protocols.core.values.db.{DescriptionE, LabelE}
import cats.instances.option._
import cats.instances.option._
import cats.instances.uuid._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyBoardingUI(
    id: UUID,
    propertyId: UUID,
    category: BoardingUI,
    label: LabelE,
    description: Option[DescriptionE]
)

object PropertyBoardingUI {
  type Id = UUID

  implicit final val propertyBoardingUIR: PropertyBoardingUI => Id = _.id

  implicit final val propertyBoardingUIPart: PropertyBoardingUI => UUID = _.propertyId
}
