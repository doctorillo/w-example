package bookingtour.protocols.parties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.LabelAPI
import bookingtour.protocols.core.values.enumeration.PointItem
import bookingtour.protocols.parties.newTypes.PointId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order, loggable)
final case class PointUI(id: PointId, parent: Option[PointId], label: LabelAPI, category: PointItem)

object PointUI {
  type Id = PointId

  implicit final val itemR0: PointUI => Id = _.id

  implicit final val itemP0: PointUI => Int = _ => 0
}
