package bookingtour.data.parties.sql.parties.companies

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.parties.values.CompanyPREP
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.query.Query0
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class CompanyCreate private (
    getOps: GetOps[UUID, CompanyPREP]
) extends ByInputOps[CompanyPREP.Create, CompanyPREP] with CreateOps[CompanyPREP.Create, CompanyPREP]
    with BatchCreateOps[CompanyPREP.Create, CompanyPREP] {

  def byInput(data: CompanyPREP.Create): Query0[CompanyPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            code,
            name,
            updated
           FROM
            companies
           WHERE
            mark_as_delete = FALSE
            AND data_id = ${data.partyId}"""
      .query[CompanyPREP.Output]
      .map(CompanyPREP.outputTransform)
  }

  def insert(data: CompanyPREP.Create): Update0 =
    sql"""INSERT INTO companies ("data_id", "code", "name") VALUES (${data.partyId}, ${data.code}, ${data.name})""".update

  def runCreate(data: CompanyPREP.Create): ConnectionIO[CompanyPREP] = {
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

object CompanyCreate {
  final def apply(): CreateOps[CompanyPREP.Create, CompanyPREP] with BatchCreateOps[CompanyPREP.Create, CompanyPREP] =
    new CompanyCreate(CompanyGet())
}
