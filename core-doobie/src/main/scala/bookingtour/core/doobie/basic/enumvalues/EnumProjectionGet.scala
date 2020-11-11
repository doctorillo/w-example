package bookingtour.core.doobie.basic.enumvalues

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.core.values.db.EnumProjectionE
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class EnumProjectionGet private (table: String)
    extends GetOps[UUID, EnumProjectionE] with EnumerationToDoobieOps {

  def get(id: UUID): query.Query0[EnumProjectionE] = {
    (const(s"""
    SELECT
      id,
      value_id,
      updated
    FROM
    	$table
    WHERE
    	mark_as_delete = FALSE
    	AND """) ++ fr"id = $id").query[EnumProjectionE.Output].map(EnumProjectionE.outputTransform)
  }
}

object EnumProjectionGet {
  final def apply(table: String): EnumProjectionGet = new EnumProjectionGet(table)
}
