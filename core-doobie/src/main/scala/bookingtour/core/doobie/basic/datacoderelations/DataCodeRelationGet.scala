package bookingtour.core.doobie.basic.datacoderelations

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.core.values.db.DataCodeRelationE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class DataCodeRelationGet private (table: String)
    extends GetOps[UUID, DataCodeRelationE] with EnumerationToDoobieOps {

  def get(id: UUID): query.Query0[DataCodeRelationE] = {
    (const(s"""
    SELECT
      id,
      data_id,
      code,
      updated
    FROM
    	$table
    WHERE
    	mark_as_delete = FALSE
    	AND """) ++ fr"id = $id")
      .query[DataCodeRelationE.Output]
      .map(DataCodeRelationE.outputTransform)
  }
}

object DataCodeRelationGet {
  final def apply(table: String): DataCodeRelationGet = new DataCodeRelationGet(table)
}
