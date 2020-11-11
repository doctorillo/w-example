package bookingtour.core.doobie.basic.labels

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.core.values.db.LabelE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class LabelGet private (table: String) extends GetOps[UUID, LabelE] with EnumerationToDoobieOps {

  def get(id: UUID): query.Query0[LabelE] = {
    (const(s"""
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
    	AND """) ++ fr"id = $id").query[LabelE.Output].map(LabelE.outputTransform)
  }
}

object LabelGet {
  final def apply(table: String): GetOps[UUID, LabelE] = new LabelGet(table)
}
