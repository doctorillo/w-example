package bookingtour.protocols.core.values.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.LabelId
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order, loggable)
final case class LabelAPI(id: LabelId, lang: LangItem, label: String)

object LabelAPI {
  type Id = LabelId

  implicit final val itemR: LabelAPI => Id = _.id

  implicit final val itemP: LabelAPI => Int = _ => 0
}
