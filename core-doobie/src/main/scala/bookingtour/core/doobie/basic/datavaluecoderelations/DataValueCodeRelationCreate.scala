package bookingtour.core.doobie.basic.datavaluecoderelations

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.DataValueCodeRelationE
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
final class DataValueCodeRelationCreate private (
    table: String,
    getOps: GetOps[UUID, DataValueCodeRelationE]
) extends CreateOps[DataValueCodeRelationE.Create, DataValueCodeRelationE]
    with BatchCreateOps[DataValueCodeRelationE.Create, DataValueCodeRelationE] {

  def byInput(data: DataValueCodeRelationE.Create): Query0[DataValueCodeRelationE] = {
    (const(s"""SELECT DISTINCT
                id,
                data_id,
                value_id,
                code,
                updated
               FROM
                $table
               WHERE
               	mark_as_delete = FALSE
               	AND """) ++ fr"data_id = ${data.dataId} AND code = ${data.code}")
      .query[DataValueCodeRelationE.Output]
      .map(DataValueCodeRelationE.outputTransform)
  }

  def insert(item: DataValueCodeRelationE.Create): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "data_id", "value_id", "code") VALUES (${item.solverId}, ${item.dataId}, ${item.valueId}, ${item.code})""").update

  def runCreate(data: DataValueCodeRelationE.Create): ConnectionIO[DataValueCodeRelationE] = {
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

object DataValueCodeRelationCreate {
  final def apply(table: String): DataValueCodeRelationCreate =
    new DataValueCodeRelationCreate(table, DataValueCodeRelationGet(table))
}
