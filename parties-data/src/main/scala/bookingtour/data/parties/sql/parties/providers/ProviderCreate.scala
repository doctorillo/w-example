package bookingtour.data.parties.sql.parties.providers

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.parties.values.ProviderPREP
import cats.syntax.applicative._
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
final class ProviderCreate private (getOps: GetOps[UUID, ProviderPREP])
    extends ByInputOps[ProviderPREP.Create, ProviderPREP] with CreateOps[ProviderPREP.Create, ProviderPREP]
    with BatchCreateOps[ProviderPREP.Create, ProviderPREP] {

  def byInput(data: ProviderPREP.Create): query.Query0[ProviderPREP] = {
    sql"""SELECT DISTINCT
            id,
            context_id,
            party_id,
            updated
           FROM
            providers
           WHERE
            mark_as_delete = FALSE
            AND context_id = ${data.ctxId}
            AND party_id = ${data.partyId}
           ORDER BY
            id"""
      .query[ProviderPREP.Output]
      .map(ProviderPREP.outputTransform)
  }

  def insert(data: ProviderPREP.Create): Update0 =
    sql"""INSERT INTO providers ("context_id", "party_id") VALUES (${data.ctxId}, ${data.partyId})""".update

  def runCreate(data: ProviderPREP.Create): ConnectionIO[ProviderPREP] = {
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

object ProviderCreate {
  final def apply(): ProviderCreate =
    new ProviderCreate(ProviderGet())
}
