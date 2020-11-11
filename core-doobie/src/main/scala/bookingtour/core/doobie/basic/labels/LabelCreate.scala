package bookingtour.core.doobie.basic.labels

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.LabelE
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
final class LabelCreate private (table: String, getOps: GetOps[UUID, LabelE])
    extends ByInputOps[LabelE.Create, LabelE] with CreateOps[LabelE.Create, LabelE]
    with BatchCreateOps[LabelE.Create, LabelE] with EnumerationToDoobieOps {

  def byInput(data: LabelE.Create): Query0[LabelE] = {
    (const(s"""SELECT
                id,
                data_id,
                lang_id,
                label,
                updated
               FROM
                $table
               WHERE
               	mark_as_delete = FALSE
               	AND """) ++ fr"data_id = ${data.dataId} AND lang_id = ${data.lang}")
      .query[LabelE.Output]
      .map(LabelE.outputTransform)
  }

  def insert(item: LabelE.Create): Update0 =
    (const(s"""INSERT INTO $table""") ++ fr"""("solver_id", "data_id", "lang_id", "label") VALUES (${item.solverId}, ${item.dataId}, ${item.lang}, ${item.label})""").update

  def runCreate(data: LabelE.Create): ConnectionIO[LabelE] = {
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

object LabelCreate {
  final def apply(table: String): ByInputOps[LabelE.Create, LabelE]
    with CreateOps[LabelE.Create, LabelE] with BatchCreateOps[LabelE.Create, LabelE] =
    new LabelCreate(table, LabelGet(table))
}
