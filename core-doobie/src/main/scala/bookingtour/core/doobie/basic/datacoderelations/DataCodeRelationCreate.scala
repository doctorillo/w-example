package bookingtour.core.doobie.basic.datacoderelations

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.DataCodeRelationE
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query.Query0
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class DataCodeRelationCreate private (table: String, getOps: GetOps[UUID, DataCodeRelationE])
    extends CreateOps[DataCodeRelationE.Create, DataCodeRelationE]
    with BatchCreateOps[DataCodeRelationE.Create, DataCodeRelationE] {

  def byInput(data: DataCodeRelationE.Create): Query0[DataCodeRelationE] = {
    (const(s"""SELECT DISTINCT
                id,
                data_id,
                code,
                updated
               FROM
                $table
               WHERE
               	mark_as_delete = FALSE
               	AND """) ++ fr"data_id = ${data.dataId} AND code = ${data.code}")
      .query[DataCodeRelationE.Output]
      .map(DataCodeRelationE.outputTransform)
  }

  def insert(item: DataCodeRelationE.Create): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "data_id", "code") VALUES (${item.solverId}, ${item.dataId}, ${item.code})""").update

  def runCreate(data: DataCodeRelationE.Create): ConnectionIO[DataCodeRelationE] = {
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

object DataCodeRelationCreate {
  final def apply(table: String): DataCodeRelationCreate =
    new DataCodeRelationCreate(table, DataCodeRelationGet(table))
}
