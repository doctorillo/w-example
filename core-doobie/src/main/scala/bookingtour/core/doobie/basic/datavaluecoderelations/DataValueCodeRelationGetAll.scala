package bookingtour.core.doobie.basic.datavaluecoderelations

import bookingtour.core.doobie.modules.GetAllOps
import bookingtour.core.doobie.modules.GetAllOps
import bookingtour.protocols.core.values.db._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class DataValueCodeRelationGetAll private (table: String) extends GetAllOps[DataValueCodeRelationE] {

  def getAll(): Query0[DataValueCodeRelationE] = {
    const(s"""
    SELECT
      id,
      data_id,
      value_id,
      code,
      updated
    FROM
      $table
    WHERE
    	mark_as_delete = FALSE
    ORDER BY
      id""")
      .query[DataValueCodeRelationE.Output]
      .map(DataValueCodeRelationE.outputTransform)
  }
}

object DataValueCodeRelationGetAll {
  final def apply(table: String): GetAllOps[DataValueCodeRelationE] =
    new DataValueCodeRelationGetAll(table)
}
