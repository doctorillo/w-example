package bookingtour.core.doobie.basic.datarelations

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.core.values.db.DataRelationE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class DataRelationGet private (table: String) extends GetOps[UUID, DataRelationE] with EnumerationToDoobieOps {

  def get(id: UUID): query.Query0[DataRelationE] = {
    (const(s"""
    SELECT
      id,
      data_id,
      value_id,
      updated
    FROM
    	$table
    WHERE
    	mark_as_delete = FALSE
    	AND """) ++ fr"id = $id").query[DataRelationE.Output].map(DataRelationE.outputTransform)
  }
}

object DataRelationGet {
  final def apply(table: String): DataRelationGet = new DataRelationGet(table)
}
