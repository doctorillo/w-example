package bookingtour.core.doobie.basic.syncs

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class SyncGet private (table: String) extends GetOps[UUID, SyncE] with EnumerationToDoobieOps {
  def get(id: UUID): query.Query0[SyncE] = {
    (const(s"""
    SELECT DISTINCT
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
    	AND """) ++ fr"id = $id")
      .query[SyncE.Output]
      .map(SyncE.outputTransform)
  }
}

object SyncGet {
  final def apply(table: String): SyncGet = new SyncGet(table)
}
