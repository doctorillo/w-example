package bookingtour.data.parties.sql.solvers

import java.util.UUID

import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.data.parties.sql.parties.persons.PersonCreateByName
import bookingtour.data.parties.sql.users.UserCreate
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.values.{PersonPREP, SolverPREP, UserPREP}
import cats.instances.all._
import cats.syntax.applicative._
import cats.syntax.traverse._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.query
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class SolverCreate private (
    getOps: GetOps[UUID, SolverPREP],
    syncOps: CreateOps[SyncE.Create, SyncE] with ByInputOps[SyncItem, SyncE],
    personCreateOps: CreateOps[PersonPREP.Create, PersonPREP],
    userCreateOps: CreateOps[UserPREP.Create, UserPREP]
) extends ByInputOps[String, SolverPREP] with CreateOps[SolverPREP.Create, SolverPREP]
    with BatchCreateOps[SolverPREP.Create, SolverPREP] {

  def byInput(email: String): query.Query0[SolverPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            email,
            password_hash,
            preferred_lang,
            updated
           FROM
            solvers
           WHERE
            mark_as_delete = FALSE
            AND email = $email"""
      .query[SolverPREP.Output]
      .map(SolverPREP.outputTransform)
  }

  def insert(data: SolverPREP.Create): Update0 =
    sql"""INSERT INTO solvers ("data_id", "email", "password_hash", "preferred_lang") VALUES (${data.personId}, ${data.email}, ${data.passwordHash}, ${data.preferredLang})""".update

  def runCreate(data: SolverPREP.Create): ConnectionIO[SolverPREP] = {
    for {
      a <- byInput(data.email).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                person <- personCreateOps.runCreate(
                           PersonPREP.Create(
                             partyId = UUID.randomUUID(),
                             firstName = data.firstName,
                             lastName = data.lastName
                           )
                         )
                id <- insert(data.copy(personId = person.id.x)).withUniqueGeneratedKeys[UUID]("id")
                _  <- syncOps.runCreate(SyncE.Create(dataId = id, sync = SolverPREP.toSync(data.syncId)))
                _ <- data.users.traverse(uc =>
                      userCreateOps.runCreate(
                        UserPREP.Create(
                          appId = data.appId,
                          solverRef = id,
                          businessPartyId = uc.companyPartyId,
                          roles = uc.roles
                        )
                      )
                    )
                c <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object SolverCreate {
  final def apply(tableSync: String): SolverCreate =
    new SolverCreate(SolverGet(), SyncCreate(tableSync), PersonCreateByName(), UserCreate())
}
