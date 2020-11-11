package bookingtour.core.doobie.basic.descriptions

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.core.values.db.DescriptionE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class DescriptionGet private (table: String) extends GetOps[UUID, DescriptionE] with EnumerationToDoobieOps {

  def get(id: UUID): query.Query0[DescriptionE] = {
    (const(s"""
    SELECT
      id,
      data_id,
      lang_id,
      data,
      updated
    FROM
    	$table
    WHERE
    	mark_as_delete = FALSE
    	AND """) ++ fr"id = $id").query[DescriptionE.Output].map(DescriptionE.outputTransform)
  }
}

object DescriptionGet {
  final def apply(table: String): DescriptionGet = new DescriptionGet(table)
}
