package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDate, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.parties.newTypes.{PartyId, SupplierGroupId, SupplierGroupMemberId}
import bookingtour.protocols.parties.newTypes.{PartyId, SupplierGroupId, SupplierGroupMemberId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class SupplierGroupMemberPREP(
    id: SupplierGroupMemberId,
    groupId: SupplierGroupId,
    memberId: PartyId,
    lifeDates: Ranges.Dates,
    stamp: Instant
)

object SupplierGroupMemberPREP {
  type Id = SupplierGroupMemberId

  implicit final val itemR0: SupplierGroupMemberPREP => Id = _.id

  implicit final val itemR1: SupplierGroupMemberPREP => Instant = _.stamp

  implicit final val itemP0: SupplierGroupMemberPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      groupId: UUID,
      memberId: UUID,
      lifeDateFrom: LocalDate,
      lifeDateTo: LocalDate,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => SupplierGroupMemberPREP =
    _.into[SupplierGroupMemberPREP]
      .withFieldComputed(_.id, x => SupplierGroupMemberId(x.id))
      .withFieldComputed(_.groupId, x => SupplierGroupId(x.groupId))
      .withFieldComputed(_.memberId, x => PartyId(x.memberId))
      .withFieldComputed(_.lifeDates, x => Ranges.Dates(x.lifeDateFrom, x.lifeDateTo))
      .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
      .transform

  @derive(order)
  final case class Create(groupId: UUID, partyId: UUID, solverId: Option[UUID] = None)

  implicit final val itemP1: Create => Int = _ => 0
}
