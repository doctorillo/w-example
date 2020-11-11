package bookingtour.protocols.excursions.values

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, ImageId}
import bookingtour.protocols.core.newtypes.quantities.Position
import bookingtour.protocols.core.values.api.{ImageAPI, LabelAPI}
import bookingtour.protocols.excursions.newTypes.ExcursionId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ExcursionImageProduct(
    id: ImageId,
    excursionId: ExcursionId,
    src: String,
    labels: List[LabelAPI],
    position: Position
)

object ExcursionImageProduct {
  type Id = ImageId

  implicit final val itemR: ExcursionImageProduct => Id = _.id

  implicit final val itemP: ExcursionImageProduct => Int = _ => 0

  final val toApi: ExcursionImageProduct => List[ImageAPI] = x =>
    x.labels.map(label =>
      ImageAPI(
        id = x.id,
        dataId = DataId(x.excursionId.x),
        src = x.src,
        label = label,
        position = x.position
      )
    )
}
