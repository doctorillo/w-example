package bookingtour.core.doobie.basic.enumvalues

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.protocols.core.values.db._
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import cats.data.NonEmptyList
import doobie.Fragments.in
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragment.Fragment.const
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class EnumProjectionGetById private (table: String)
    extends GetByIdListOps[UUID, EnumProjectionE] with EnumerationToDoobieOps {

  def get(id: List[UUID]): Query0[EnumProjectionE] = {
    (const(s"""
    SELECT DISTINCT
      id,
      value_id,
      updated
    FROM
      $table
    WHERE
      mark_as_delete = FALSE
      AND """) ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[EnumProjectionE.Output]
      .map(EnumProjectionE.outputTransform)
  }
}

object EnumProjectionGetById {
  final def apply(table: String): GetByIdListOps[UUID, EnumProjectionE] =
    new EnumProjectionGetById(table)
}
