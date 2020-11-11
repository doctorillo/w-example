package bookingtour.data.parties.sql.contexts.app

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, GetOps}
import bookingtour.data.parties.sql.contexts.langs.AppLangCreate
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import bookingtour.protocols.parties.values.{AppLangPREP, AppPREP}
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
final class AppPartnerCreate private (
    langOps: BatchCreateOps[AppLangPREP.Create, AppLangPREP],
    getOps: GetOps[UUID, AppPREP]
) extends CreateOps[AppPREP.Create, AppPREP] with BatchCreateOps[AppPREP.Create, AppPREP]
    with EnumerationToDoobieOps {

  def byInput(data: AppPREP.Create): Query0[AppPREP] = {
    sql"""
         |SELECT DISTINCT
         |	id,
         |  ident,
         |  updated
         |FROM
         |	apps
         |WHERE
         |  ident = ${data.ident}
         |	AND mark_as_delete = FALSE
         |""".stripMargin.query[AppPREP.Output].map(AppPREP.outputTransform)
  }

  def insert(data: AppPREP.Create): Update0 =
    sql"""INSERT INTO apps ("ident") VALUES (${data.ident})""".update

  def runCreate(data: AppPREP.Create): ConnectionIO[AppPREP] = {
    for {
      a <- byInput(data).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]
            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                _  <- langOps.runCreateList(AppLangPREP.create(id))
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object AppPartnerCreate {
  final def apply(): AppPartnerCreate =
    new AppPartnerCreate(AppLangCreate(), AppPartnerGet())
}
