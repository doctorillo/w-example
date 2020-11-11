package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.parties.agg.basic.PersonAgg
import bookingtour.protocols.parties.newTypes.{PartyId, PersonId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PersonPREP(
    id: PersonId,
    partyId: PartyId,
    firstName: String,
    lastName: String,
    stamp: Instant
)

object PersonPREP {
  type Id = PersonId

  implicit final val itemR: PersonPREP => Id = _.id

  implicit final val itemR1: PersonPREP => Instant = _.stamp

  implicit final val itemP: PersonPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      partyId: UUID,
      firstName: String,
      lastName: String,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => PersonPREP = _.into[PersonPREP]
    .withFieldComputed(_.id, x => PersonId(x.id))
    .withFieldComputed(_.partyId, x => PartyId(x.partyId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      partyId: UUID,
      firstName: String,
      lastName: String,
      solverId: Option[UUID] = None
  )

  implicit final val itemP0: Create => Int = _ => 0

  @derive(order)
  final case class CreateByName(
      firstName: String,
      lastName: String,
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: CreateByName => Int = _ => 0

  final val toAgg: PersonPREP => PersonAgg = _.into[PersonAgg].transform
}
