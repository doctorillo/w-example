package bookingtour.protocols.parties.api

import java.util.UUID

import bookingtour.protocols.core._
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SolverV(
    uuid: UUID,
    lookId: Option[Int],
    email: String,
    password: String,
    party: PartyV,
    users: List[UserV],
    active: UserV
)

object SolverV {
  type Id = UUID

  implicit val solverVR: SolverV => Id = _.uuid
}
