package bookingtour.core.doobie.basic.datavaluecoderelations

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.core.values.db.DataValueCodeRelationE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class DataValueCodeRelationGet private (table: String)
    extends GetOps[UUID, DataValueCodeRelationE] with EnumerationToDoobieOps {

  def get(id: UUID): query.Query0[DataValueCodeRelationE] = {
    (const(s"""
    SELECT DISTINCT
      id,
      data_id,
      value_id,
      code,
      updated
    FROM
    	$table
    WHERE
    	mark_as_delete = FALSE
    	AND """) ++ fr"id = $id")
      .query[DataValueCodeRelationE.Output]
      .map(DataValueCodeRelationE.outputTransform)
  }
}

object DataValueCodeRelationGet {
  final def apply(table: String): DataValueCodeRelationGet = new DataValueCodeRelationGet(table)
}
