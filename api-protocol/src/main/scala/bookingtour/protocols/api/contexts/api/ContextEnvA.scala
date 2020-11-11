package bookingtour.protocols.api.contexts.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{ContextRoleItem, LangItem}
import bookingtour.protocols.parties.agg.basic.SolverDataAgg
import bookingtour.protocols.parties.newTypes.{Email, PasswordHash, SolverId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class ContextEnvA(
    solverId: SolverId,
    solverName: String,
    email: Email,
    password: PasswordHash,
    workspaces: List[ContextWorkspaceA],
    preferredLang: LangItem
)

object ContextEnvA {
  type Id = SolverId

  implicit final val itemR: ContextEnvA => Id = _.solverId

  // TODO |ZZZ| hard-coded roles for providers
  final def of(agg: SolverDataAgg): ContextEnvA = {
    val p = agg.users
      .groupBy(_.provider.ctx.ctxType)
      .toList
      .foldLeft(List.empty[ContextWorkspaceA]) { (acc, x) =>
        val pxs = x._2.foldLeft(List.empty[ContextPartyA]) { (zacc, z) =>
          zacc :+ ContextPartyA(
            userId = z.id,
            businessPartyId = z.provider.company.party.id,
            businessParty = z.provider.company.name,
            roles = List(
              ContextRoleA(
                id = z.provider.id.x,
                ctx = z.provider.ctx.ctxType,
                role = ContextRoleItem.Provider
              )
            ),
            securities = z.roles
          )
        }
        acc :+ ContextWorkspaceA(
          ctx = x._2.head.provider.ctx.ctxType,
          parties = pxs
        )
      }
    ContextEnvA(
      solverId = agg.id,
      solverName = s"${agg.person.lastName} ${agg.person.firstName}",
      email = agg.email,
      password = agg.password,
      workspaces = p,
      preferredLang = LangItem.Ru
    )
  }
}
