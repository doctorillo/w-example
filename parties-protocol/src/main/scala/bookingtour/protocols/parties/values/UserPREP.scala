package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.RoleItem
import bookingtour.protocols.parties.newTypes.{AppId, PartyId, SolverId, UserId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class UserPREP(
    id: UserId,
    solverId: SolverId,
    appId: AppId,
    businessPartyId: PartyId,
    roles: List[RoleItem],
    stamp: Instant
)

object UserPREP {
  type Id = UserId

  implicit final val itemR0: UserPREP => Id = _.id

  implicit final val itemR1: UserPREP => Instant = _.stamp

  implicit final val itemP0: UserPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      solverId: UUID,
      appId: UUID,
      businessPartyId: UUID,
      roles: Array[Int],
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => UserPREP = _.into[UserPREP]
    .withFieldComputed(_.id, x => UserId(x.id))
    .withFieldComputed(_.solverId, x => SolverId(x.solverId))
    .withFieldComputed(_.appId, x => AppId(x.appId))
    .withFieldComputed(_.businessPartyId, x => PartyId(x.businessPartyId))
    .withFieldComputed(_.roles, _.roles.toList.map(RoleItem.withValue(_)))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      appId: UUID,
      solverRef: UUID,
      businessPartyId: UUID,
      roles: List[Int],
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0
}
