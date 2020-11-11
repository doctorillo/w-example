package bookingtour.protocols.parties.api.ui

import java.time.Instant

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.parties.newTypes.{Email, PasswordHash, SolverId}
import bookingtour.protocols.parties.newTypes.{Email, PasswordHash, SolverId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class ContextEnvUI(
    solverId: SolverId,
    solverName: String,
    solverHash: PasswordHash,
    email: Email,
    preferredLang: LangItem,
    workspace: WorkspaceUI,
    workspaces: List[WorkspaceUI],
    created: Instant,
    updated: Instant
)

object ContextEnvUI {
  type Id = SolverId

  // TODO |ZZZ| create efficient logic
  /*final def of(env: ContextEnvA): ContextEnvUI = {
    val _workspaces = for {
      a <- env.workspaces
      b <- a.parties
    } yield {
      WorkspaceUI(
        userId = b.userId,
        businessPartyId = b.businessPartyId,
        businessParty = b.businessParty,
        securities = b.securities
      )
    }
    val __workspaces = _workspaces.groupBy(_.userId).toNel.map {
      case (_, xs) =>
        WorkspaceUI(
          userId = xs.head.userId,
          businessPartyId = xs.head.businessPartyId,
          businessParty = xs.head.businessParty,
          securities = xs.head.securities
        )
    }
    ContextEnvUI(
      solverId = env.solverId,
      solverName = env.solverName,
      solverHash = env.password,
      email = env.email,
      preferredLang = env.preferredLang,
      workspace = __workspaces.head,
      workspaces = NonEmptyChain.fromNonEmptyList(__workspaces)
    )
  }*/

  /*final def of(env: BusinessSolverUI): ContextEnvUI = {
    val _workspaces = NonEmptyChain.fromChainUnsafe(
      for {
        user <- env.users
      } yield WorkspaceUI(
        userId = user.id,
        businessPartyId = user.company.partyId,
        businessParty = user.company.name,
        securities = NonEmptyChain.fromChainUnsafe(user.roles)
      )
    )
    ContextEnvUI(
      solverId = env.id,
      solverName = env.personName,
      solverHash = env.password,
      email = env.email,
      preferredLang = LangItem.Ru,
      workspace = _workspaces.head,
      workspaces = _workspaces
    )
  }*/
}
