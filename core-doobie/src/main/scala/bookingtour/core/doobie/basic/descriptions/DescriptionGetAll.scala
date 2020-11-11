package bookingtour.core.doobie.basic.descriptions

import bookingtour.core.doobie.modules.GetAllOps
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
final class DescriptionGetAll private (table: String) extends GetAllOps[DescriptionE] with EnumerationToDoobieOps {

  def getAll(): Query0[DescriptionE] = {
    const(s"""
    SELECT DISTINCT
      id,
      data_id,
      lang_id,
      data,
      updated
    FROM
      $table
    WHERE
    	mark_as_delete = FALSE
    ORDER BY
      id""")
      .query[DescriptionE.Output]
      .map(DescriptionE.outputTransform)
  }
}

object DescriptionGetAll {
  final def apply(table: String): GetAllOps[DescriptionE] = new DescriptionGetAll(table)
}
