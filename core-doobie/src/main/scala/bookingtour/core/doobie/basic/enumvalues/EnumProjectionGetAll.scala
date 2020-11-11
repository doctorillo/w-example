package bookingtour.core.doobie.basic.enumvalues

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
final class EnumProjectionGetAll private (table: String)
    extends GetAllOps[EnumProjectionE] with EnumerationToDoobieOps {

  def getAll(): Query0[EnumProjectionE] = {
    const(s"""
    SELECT DISTINCT
      id,
      value_id,
      updated
    FROM
      $table
    WHERE
    	mark_as_delete = FALSE
    ORDER BY
      id""")
      .query[EnumProjectionE.Output]
      .map(EnumProjectionE.outputTransform)
  }
}

object EnumProjectionGetAll {
  final def apply(table: String): GetAllOps[EnumProjectionE] = new EnumProjectionGetAll(table)
}
