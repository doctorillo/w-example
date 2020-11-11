package bookingtour.core.doobie.basic.datavaluecoderelations

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.protocols.core.values.db._
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
final class DataValueCodeRelationGetById private (table: String) extends GetByIdListOps[UUID, DataValueCodeRelationE] {

  def get(id: List[UUID]): Query0[DataValueCodeRelationE] = {
    (const(s"""
    SELECT DISTINCT
      id,
      data_id,
      value_id,
      code,
      updated
    FROM
      $table
    WHERE
      mark_as_delete = FALSE
      AND """) ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[DataValueCodeRelationE.Output]
      .map(DataValueCodeRelationE.outputTransform)
  }
}

object DataValueCodeRelationGetById {
  final def apply(table: String): GetByIdListOps[UUID, DataValueCodeRelationE] =
    new DataValueCodeRelationGetById(table)
}
