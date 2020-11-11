package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{CompanyId, PartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class CompanyPREP(
    id: CompanyId,
    partyId: PartyId,
    code: String,
    name: String,
    stamp: Instant
)

object CompanyPREP {
  type Id = CompanyId

  implicit final val itemR: CompanyPREP => Id = _.id

  implicit final val itemR1: CompanyPREP => Instant = _.stamp

  implicit final val itemP: CompanyPREP => Int = _ => 0

  final case class Output(id: UUID, partyId: UUID, code: String, name: String, stamp: LocalDateTime)

  implicit final val outputTransform: Output => CompanyPREP = _.into[CompanyPREP]
    .withFieldComputed(_.id, x => CompanyId(x.id))
    .withFieldComputed(_.partyId, x => PartyId(x.partyId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      partyId: UUID,
      code: String,
      name: String,
      solverId: Option[UUID] = None
  )

  implicit final val itemP0: Create => Int = _ => 0
}
