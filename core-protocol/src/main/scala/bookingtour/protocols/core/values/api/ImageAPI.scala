package bookingtour.protocols.core.values.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, ImageId, LabelId}
import bookingtour.protocols.core.newtypes.quantities.Position
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order, loggable)
final case class ImageAPI(
    id: ImageId,
    dataId: DataId,
    src: String,
    label: LabelAPI,
    position: Position
)

object ImageAPI {
  type Id = LabelId

  implicit final val itemR: ImageAPI => Id = _.label.id

  implicit final val itemP: ImageAPI => DataId = _.dataId
}
