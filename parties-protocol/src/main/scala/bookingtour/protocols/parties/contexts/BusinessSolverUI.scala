package bookingtour.protocols.parties.contexts

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{Email, PasswordHash, PersonId, SolverId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class BusinessSolverUI(
    id: SolverId,
    email: Email,
    password: PasswordHash,
    personId: PersonId,
    personName: String,
    users: List[BusinessUserUI]
)

object BusinessSolverUI {
  type Id = SolverId

  implicit final val itemR: BusinessSolverUI => Id = _.id

  implicit final val itemP: BusinessSolverUI => Int = _ => 0
}
