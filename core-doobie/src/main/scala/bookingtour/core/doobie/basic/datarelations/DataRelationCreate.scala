package bookingtour.core.doobie.basic.datarelations

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.DataRelationE
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
final class DataRelationCreate private (table: String, getOps: GetOps[UUID, DataRelationE])
    extends CreateOps[DataRelationE.Create, DataRelationE] with BatchCreateOps[DataRelationE.Create, DataRelationE] {

  def byInput(data: DataRelationE.Create): Query0[DataRelationE] = {
    (const(s"""SELECT DISTINCT
                id,
                data_id,
                value_id,
                updated
               FROM
                $table
               WHERE
               	mark_as_delete = FALSE
               	AND """) ++ fr"data_id = ${data.dataId} AND value_id = ${data.valueId}")
      .query[DataRelationE.Output]
      .map(DataRelationE.outputTransform)
  }

  def insert(item: DataRelationE.Create): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "data_id", "value_id") VALUES (${item.solverId}, ${item.dataId}, ${item.valueId})""").update

  def runCreate(data: DataRelationE.Create): ConnectionIO[DataRelationE] = {
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

object DataRelationCreate {
  final def apply(
      table: String
  ): CreateOps[DataRelationE.Create, DataRelationE] with BatchCreateOps[DataRelationE.Create, DataRelationE] =
    new DataRelationCreate(table, DataRelationGet(table))
}
