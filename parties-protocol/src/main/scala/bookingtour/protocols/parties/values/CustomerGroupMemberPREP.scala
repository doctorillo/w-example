package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, CustomerGroupMemberId, PartyId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class CustomerGroupMemberPREP(
    id: CustomerGroupMemberId,
    groupId: CustomerGroupId,
    memberId: PartyId,
    lifeDates: Ranges.Dates,
    stamp: Instant
)

object CustomerGroupMemberPREP {
  type Id = CustomerGroupMemberId

  implicit final val itemR0: CustomerGroupMemberPREP => Id = _.id

  implicit final val itemR1: CustomerGroupMemberPREP => Instant = _.stamp

  implicit final val itemP0: CustomerGroupMemberPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      groupId: UUID,
      memberId: UUID,
      lifeDateFrom: LocalDate,
      lifeDateTo: LocalDate,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => CustomerGroupMemberPREP =
    _.into[CustomerGroupMemberPREP]
      .withFieldComputed(_.id, x => CustomerGroupMemberId(x.id))
      .withFieldComputed(_.groupId, x => CustomerGroupId(x.groupId))
      .withFieldComputed(_.memberId, x => PartyId(x.memberId))
      .withFieldComputed(_.lifeDates, x => Ranges.Dates(x.lifeDateFrom, x.lifeDateTo))
      .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
      .transform

  @derive(order)
  final case class Create(groupId: UUID, partyId: UUID, solverId: Option[UUID] = None)

  implicit final val itemP1: Create => Int = _ => 0
}
