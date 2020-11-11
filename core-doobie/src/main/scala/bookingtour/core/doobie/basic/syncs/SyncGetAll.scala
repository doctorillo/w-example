package bookingtour.core.doobie.basic.syncs

import bookingtour.core.doobie.modules.GetAllOps
import bookingtour.protocols.core.values.db._
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class SyncGetAll private (table: String) extends GetAllOps[SyncE] with EnumerationToDoobieOps {

  def getAll(): Query0[SyncE] = {
    const(s"""
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
    ORDER BY
      id""")
      .query[SyncE.Output]
      .map(SyncE.outputTransform)
  }
}

object SyncGetAll {
  final def apply(table: String): GetAllOps[SyncE] = new SyncGetAll(table)
}
