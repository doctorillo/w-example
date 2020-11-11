package bookingtour.protocols.parties.agg

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{RoleItem, SyncItem}
import bookingtour.protocols.parties.newTypes._
import bookingtour.protocols.parties.newTypes.{CompanyId, Email, PartyId, PasswordHash, PersonId, SolverId, UserId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SolverUserAGG(
    id: SolverId,
    email: Email,
    password: PasswordHash,
    personId: PersonId,
    personName: String,
    userId: UserId,
    userSyncs: List[SyncItem],
    userBusinessCompanyId: CompanyId,
    userBusinessPartyId: PartyId,
    userBusinessPartyCode: String,
    userBusinessPartyName: String,
    roles: List[RoleItem]
)

object SolverUserAGG {
  type Id = SolverId

  implicit final val solverUserAGGR: SolverUserAGG => Id = _.id

  implicit final val solverUserAGGPart: SolverUserAGG => (PartyId, SolverUserAGG) = x => (x.userBusinessPartyId, x)
}
