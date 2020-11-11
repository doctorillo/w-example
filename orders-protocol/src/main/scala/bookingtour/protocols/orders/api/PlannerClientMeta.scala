package bookingtour.protocols.orders.api

import java.time.LocalDate

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.GenderItem
import bookingtour.protocols.orders.newTypes.PlannerClientId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerClientMeta(
    id: PlannerClientId,
    firstName: Option[String],
    lastName: Option[String],
    gender: Option[GenderItem],
    birthDay: Option[LocalDate],
    passport: PlannerPassportMeta
)
