package bookingtour.core.doobie.basic.labels

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
final class LabelGetAll private (table: String) extends GetAllOps[LabelE] with EnumerationToDoobieOps {

  def getAll(): Query0[LabelE] = {
    const(s"""
    SELECT DISTINCT
      id,
      data_id,
      lang_id,
      label,
      updated
    FROM
      $table
    WHERE
    	mark_as_delete = FALSE
    ORDER BY
      id""")
      .query[LabelE.Output]
      .map(LabelE.outputTransform)
  }
}

object LabelGetAll {
  final def apply(table: String): GetAllOps[LabelE] = new LabelGetAll(table)
}
