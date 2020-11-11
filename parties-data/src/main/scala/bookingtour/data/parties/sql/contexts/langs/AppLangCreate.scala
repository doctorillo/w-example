package bookingtour.data.parties.sql.contexts.langs

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, GetOps}
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import bookingtour.protocols.parties.values.AppLangPREP
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
final class AppLangCreate private (
    getOps: GetOps[UUID, AppLangPREP]
) extends CreateOps[AppLangPREP.Create, AppLangPREP] with BatchCreateOps[AppLangPREP.Create, AppLangPREP]
    with EnumerationToDoobieOps {

  def byInput(data: AppLangPREP.Create): Query0[AppLangPREP] = {
    sql"""
         SELECT DISTINCT
         	id,
         	data_id,
         	lang_id,
          active,
          updated
         FROM
         	app_langs
         WHERE
           data_id = ${data.appId}
           AND lang_id = ${data.lang}
         	 AND mark_as_delete = FALSE
         """.query[AppLangPREP.Output].map(AppLangPREP.outputTransform)
  }

  def insert(data: AppLangPREP.Create): Update0 =
    sql"""INSERT INTO app_langs ("solver_id", "data_id", "lang_id", "active") VALUES (${data.solverId}, ${data.appId}, ${data.lang}, ${data.active})""".update

  def runCreate(data: AppLangPREP.Create): ConnectionIO[AppLangPREP] = {
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

object AppLangCreate {
  final def apply(): CreateOps[AppLangPREP.Create, AppLangPREP] with BatchCreateOps[AppLangPREP.Create, AppLangPREP] =
    new AppLangCreate(AppLangGet())
}
