package bookingtour.protocols.core.values.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{EnumId, EnumValue, LabelId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class EnumAPI(id: EnumId, value: EnumValue, label: LabelAPI)

object EnumAPI {
  type Id = LabelId

  implicit final val itemR0: EnumAPI => Id = _.label.id

  implicit final val itemP0: EnumAPI => Int = _ => 0
}
