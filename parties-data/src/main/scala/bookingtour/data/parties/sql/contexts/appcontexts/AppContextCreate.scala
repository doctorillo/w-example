package bookingtour.data.parties.sql.contexts.appcontexts

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, GetOps}
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import bookingtour.protocols.parties.values.AppContextPREP
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
final class AppContextCreate private (
    getOps: GetOps[UUID, AppContextPREP]
) extends CreateOps[AppContextPREP.Create, AppContextPREP] with BatchCreateOps[AppContextPREP.Create, AppContextPREP]
    with EnumerationToDoobieOps {

  def byInput(data: AppContextPREP.Create): Query0[AppContextPREP] = {
    sql"""SELECT DISTINCT
         	id,
         	data_id,
         	value_id,
          code,
          updated
         FROM
         	app_contexts
         WHERE
           data_id = ${data.appId}
           AND value_id = ${data.contextItem}
         	 AND mark_as_delete = FALSE
         """.query[AppContextPREP.Output].map(AppContextPREP.outputTransform)
  }

  def insert(data: AppContextPREP.Create): Update0 =
    sql"""INSERT INTO app_contexts ("solver_id", "data_id", "value_id", "code") VALUES (${data.solverId}, ${data.appId}, ${data.contextItem}, ${data.code})""".update

  def runCreate(data: AppContextPREP.Create): ConnectionIO[AppContextPREP] = {
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

object AppContextCreate {
  final def apply()
      : CreateOps[AppContextPREP.Create, AppContextPREP] with BatchCreateOps[AppContextPREP.Create, AppContextPREP] =
    new AppContextCreate(AppContextGet())
}
