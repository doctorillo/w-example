package bookingtour.data.parties.sql.users

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.parties.values.UserPREP
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class UserCreate private (
    getOps: GetOps[UUID, UserPREP]
) extends ByInputOps[UserPREP.Create, UserPREP] with CreateOps[UserPREP.Create, UserPREP]
    with BatchCreateOps[UserPREP.Create, UserPREP] {

  def byInput(data: UserPREP.Create): Query0[UserPREP] = {
    sql"""SELECT DISTINCT
            id,
            solver_ref_id,
            app_id,
            business_party_id,
            roles,
            updated
           FROM
            users
           WHERE
            mark_as_delete = FALSE
            AND solver_ref_id = ${data.solverRef}
            AND business_party_id = ${data.businessPartyId}"""
      .query[UserPREP.Output]
      .map(UserPREP.outputTransform)
  }

  def insert(data: UserPREP.Create): Update0 =
    sql"""INSERT INTO users ("solver_ref_id", "app_id", "business_party_id", "roles") VALUES (${data.solverRef}, ${data.appId}, ${data.businessPartyId}, ${data.roles})""".update

  def runCreate(data: UserPREP.Create): ConnectionIO[UserPREP] = {
    for {
      a <- byInput(data).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object UserCreate {
  final def apply(): UserCreate =
    new UserCreate(UserGet())
}
