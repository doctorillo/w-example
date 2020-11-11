package bookingtour.protocols.orders.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.PointItem
import bookingtour.protocols.parties.newTypes.PointId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PointOption(value: PointId, category: PointItem, label: String)
