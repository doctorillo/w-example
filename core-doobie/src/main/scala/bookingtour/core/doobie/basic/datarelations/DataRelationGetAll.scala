package bookingtour.core.doobie.basic.datarelations

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
final class DataRelationGetAll private (table: String) extends GetAllOps[DataRelationE] {

  def getAll(): Query0[DataRelationE] = {
    const(s"""
    SELECT DISTINCT
      id,
      data_id,
      value_id,
      updated
    FROM
      $table
    WHERE
    	mark_as_delete = FALSE
    ORDER BY
      id""")
      .query[DataRelationE.Output]
      .map(DataRelationE.outputTransform)
  }
}

object DataRelationGetAll {
  final def apply(table: String): DataRelationGetAll = new DataRelationGetAll(table)
}
