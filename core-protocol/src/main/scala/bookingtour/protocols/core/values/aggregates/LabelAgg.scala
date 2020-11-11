package bookingtour.protocols.core.values.aggregates

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.LabelId
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class LabelAgg(
    id: LabelId,
    lang: LangItem,
    label: String
)

object LabelAgg {
  type Id = LabelId

  implicit val labelAggR: LabelAgg => Id = _.id

  implicit final val labelAggPart: LabelAgg => Int = _ => 0

  implicit final class LabelAggOps(private val self: LabelAgg) {
    def toApi: LabelAPI = LabelAPI(id = self, lang = self.lang, label = self.label)
  }
}
