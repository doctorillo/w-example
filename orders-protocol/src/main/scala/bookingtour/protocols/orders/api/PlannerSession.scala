package bookingtour.protocols.orders.api

import java.time.LocalDateTime

import bookingtour.protocols.core._
import bookingtour.protocols.doobie.types.JsonbToJson._
import bookingtour.protocols.orders.newTypes.{PlannerSessionId, PlannerSessionPointId}
import bookingtour.protocols.parties.newTypes.{PartyId, SolverId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import doobie.util.{Get, Put}

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerSession(
    id: PlannerSessionId,
    created: LocalDateTime,
    updated: LocalDateTime,
    solverId: SolverId,
    customerId: PartyId,
    customerName: String,
    identCode: String,
    clients: List[PlannerClient],
    points: List[PlannerSessionPoint],
    activePoint: Option[PlannerSessionPointId],
    title: Option[String],
    notes: Option[String],
    deleted: Boolean,
    bookingStep: Int
)

object PlannerSession {
  type Id = PlannerSessionId

  implicit val itemR0: PlannerSession => Id = _.id

  implicit val itemP0: PlannerSession => SolverId = _.solverId
  implicit val itemP1: PlannerSession => Int      = _ => 0

  implicit def itemGet: Get[PlannerSession] = classGet[PlannerSession]
  implicit def itemPut: Put[PlannerSession] = classPut[PlannerSession]
}
