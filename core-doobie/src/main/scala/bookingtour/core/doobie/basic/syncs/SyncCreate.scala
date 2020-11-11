package bookingtour.core.doobie.basic.syncs

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
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
final class SyncCreate private (table: String, getOps: GetOps[UUID, SyncE])
    extends ByInputOps[SyncItem, SyncE] with CreateOps[SyncE.Create, SyncE] with BatchCreateOps[SyncE.Create, SyncE]
    with EnumerationToDoobieOps {

  def byInput(data: SyncItem): Query0[SyncE] = {
    (const(s"""
              SELECT
               id,
               data_id,
               source_id,
               source,
               active,
               updated
              FROM
               $table
              WHERE
              	mark_as_delete = FALSE
              	AND """) ++ fr"""source = $data::jsonb""")
      .query[SyncE.Output]
      .map(SyncE.outputTransform)
  }

  def insert(item: SyncE.Create): Update0 =
    (const(s"""INSERT INTO $table ("solver_id", "data_id", "source_id", "source")""") ++ fr""" VALUES (${item.solverId}, ${item.dataId}, ${item.sync.source}, ${item.sync}::jsonb)""").update

  def runCreate(data: SyncE.Create): ConnectionIO[SyncE] = {
    for {
      a <- byInput(data.sync).option
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

object SyncCreate {
  final def apply(
      table: String
  ): ByInputOps[SyncItem, SyncE] with CreateOps[SyncE.Create, SyncE] with BatchCreateOps[SyncE.Create, SyncE] =
    new SyncCreate(table, SyncGet(table))
}
