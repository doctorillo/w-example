package bookingtour.protocols.properties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.ImageId
import bookingtour.protocols.core.values.db.LabelE
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
final case class PropertyImageAGG(
    id: ImageId,
    propertyId: PropertyId,
    src: String,
    labels: List[LabelE],
    position: Int
)

object PropertyImageAGG {
  type Id = ImageId

  implicit val itemR: PropertyImageAGG => Id = _.id

  implicit final val itemP: PropertyImageAGG => PropertyId = _.propertyId
}
