package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.{LangItem, SyncItem}
import bookingtour.protocols.interlook.source.newTypes.LookSolverId
import bookingtour.protocols.parties.newTypes.{Email, PasswordHash, PersonId, SolverId}
import bookingtour.protocols.parties.newTypes.{Email, PasswordHash, PersonId, SolverId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SolverPREP(
    id: SolverId,
    personId: PersonId,
    email: Email,
    password: PasswordHash,
    preferredLang: LangItem,
    stamp: Instant
)

object SolverPREP {
  type Id = SolverId

  implicit final val itemR0: SolverPREP => Id = _.id

  implicit final val itemR1: SolverPREP => Instant = _.stamp

  implicit final val itemP0: SolverPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      personId: UUID,
      email: String,
      password: String,
      preferredLang: Int,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => SolverPREP = _.into[SolverPREP]
    .withFieldComputed(_.id, x => SolverId(x.id))
    .withFieldComputed(_.personId, x => PersonId(x.personId))
    .withFieldComputed(_.email, x => Email(x.email))
    .withFieldComputed(_.password, x => PasswordHash(x.password))
    .withFieldComputed(_.preferredLang, x => LangItem.withValue(x.preferredLang))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class UserCreate(companyPartyId: UUID, roles: List[Int])

  @derive(order)
  final case class Create(
      appId: UUID,
      syncId: Option[Int],
      personId: UUID,
      email: String,
      passwordHash: String,
      preferredLang: Int,
      firstName: String,
      lastName: String,
      users: List[UserCreate],
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  final case class SolverInterLookSync(id: SolverId, sync: Option[LookSolverId])

  final val toSync: Option[Int] => SyncItem.InterLook = x => SyncItem.InterLook(id = 0, categoryId = x)

  final val fromSync: SyncE => Option[SolverInterLookSync] =
    _.askInterLook.map(x => SolverInterLookSync(id = x._1.x, sync = x._3.map(z => LookSolverId(z))))
}
