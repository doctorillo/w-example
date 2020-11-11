package bookingtour.protocols.core.values.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, DescriptionId}
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
final case class DescriptionAPI(id: DescriptionId, dataId: DataId, lang: LangItem, data: String)

object DescriptionAPI {
  type Id = DescriptionId

  implicit final val itemR0: DescriptionAPI => Id = _.id

  implicit final val itemP0: DescriptionAPI => DataId = _.dataId
}
