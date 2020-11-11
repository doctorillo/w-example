package bookingtour.protocols.properties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents._
import bookingtour.protocols.core.newtypes.quantities.Position
import bookingtour.protocols.properties.newTypes.PropertyId
import bookingtour.protocols.properties.newTypes.PropertyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyImageProduct(
    id: ImageId,
    property: PropertyId,
    src: String,
    position: Position
)

object PropertyImageProduct {
  type Id = ImageId

  implicit final val itemR: PropertyImageProduct => Id = _.id

  implicit final val itemP: PropertyImageProduct => PropertyId = _.property
}
