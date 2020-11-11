package bookingtour.core.doobie.basic.datarelations

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
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
final class DataRelationGetById private (table: String)
    extends GetByIdListOps[UUID, DataRelationE] with EnumerationToDoobieOps {

  def get(id: List[UUID]): Query0[DataRelationE] = {
    (const(s"""
    SELECT
      id,
      data_id,
      value_id,
      updated
    FROM
      $table
    WHERE
      mark_as_delete = FALSE
      AND """) ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[DataRelationE.Output]
      .map(DataRelationE.outputTransform)
  }
}

object DataRelationGetById {
  final def apply(table: String): DataRelationGetById = new DataRelationGetById(table)
}
