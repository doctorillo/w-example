package bookingtour.protocols.orders.api

import java.time.LocalDate

import bookingtour.protocols.core._
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerPassportMeta(
    serial: Option[String],
    number: Option[String],
    state: Option[String],
    expiredAt: Option[LocalDate]
)
