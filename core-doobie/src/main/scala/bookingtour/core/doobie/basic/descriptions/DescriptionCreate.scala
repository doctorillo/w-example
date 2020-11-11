package bookingtour.core.doobie.basic.descriptions

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.DescriptionE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query.Query0
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class DescriptionCreate private (table: String, getOps: GetOps[UUID, DescriptionE])
    extends ByInputOps[DescriptionE.Create, DescriptionE] with CreateOps[DescriptionE.Create, DescriptionE]
    with BatchCreateOps[DescriptionE.Create, DescriptionE] with EnumerationToDoobieOps {

  def byInput(data: DescriptionE.Create): Query0[DescriptionE] = {
    (const(s"""SELECT
                id,
                data_id,
                lang_id,
                data,
                updated
               FROM
                $table
               WHERE
               	mark_as_delete = FALSE
               	AND """) ++ fr"data_id = ${data.dataId} AND lang_id = ${data.lang}")
      .query[DescriptionE.Output]
      .map(DescriptionE.outputTransform)
  }

  def insert(item: DescriptionE.Create): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "data_id", "lang_id", "data") VALUES (${item.solverId}, ${item.dataId}, ${item.lang}, ${item.data})""").update

  def runCreate(data: DescriptionE.Create): ConnectionIO[DescriptionE] = {
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

object DescriptionCreate {
  final def apply(table: String): DescriptionCreate =
    new DescriptionCreate(table, DescriptionGet(table))
}
