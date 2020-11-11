package bookingtour.protocols.parties.agg.basic

import java.time.Instant

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.parties.api.ui.{ContextEnvUI, WorkspaceUI}
import bookingtour.protocols.parties.newTypes.{Email, PasswordHash, SolverId}
import cats.instances.all._
import cats.syntax.order._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SolverDataAgg(
    id: SolverId,
    email: Email,
    password: PasswordHash,
    person: PersonAgg,
    users: List[UserAgg]
)

object SolverDataAgg {
  type Id = SolverId

  implicit final val itemR0: SolverDataAgg => Id = _.id

  implicit final val itemP0: SolverDataAgg => Int = _ => 0

  implicit final val toContextEnvUI: (LangItem, Instant, Instant, SolverDataAgg) => ContextEnvUI =
    (lang, created, updated, data) => {
      val wxs = data.users.map(UserAgg.toWorkspaceUI(_)).foldLeft(List.empty[WorkspaceUI]) { (acc, x) =>
        acc.find(_.userId === x.userId) match {
          case Some(value) =>
            acc.filterNot(_ === value) :+ value
              .copy(securities = (value.securities ++ x.securities).distinct)
          case None =>
            acc :+ x
        }
      }
      data
        .into[ContextEnvUI]
        .withFieldComputed(_.solverId, _.id)
        .withFieldComputed(_.solverName, x => s"${x.person.lastName} ${x.person.firstName}")
        .withFieldComputed(_.solverHash, _.password)
        .withFieldComputed(_.preferredLang, _ => lang)
        .withFieldComputed(_.workspace, _ => wxs.head)
        .withFieldComputed(_.workspaces, _ => wxs)
        .withFieldComputed(_.created, _ => created)
        .withFieldComputed(_.updated, _ => updated)
        .transform
    }
}
