package bookingtour.core.doobie.basic.datacoderelations

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
final class DataCodeRelationGetAll private (table: String) extends GetAllOps[DataCodeRelationE] {

  def getAll(): Query0[DataCodeRelationE] = {
    const(s"""
    SELECT
      id,
      data_id,
      code,
      updated
    FROM
      $table
    WHERE
    	mark_as_delete = FALSE
    ORDER BY
      id""")
      .query[DataCodeRelationE.Output]
      .map(DataCodeRelationE.outputTransform)
  }
}

object DataCodeRelationGetAll {
  final def apply(table: String): DataCodeRelationGetAll = new DataCodeRelationGetAll(table)
}
