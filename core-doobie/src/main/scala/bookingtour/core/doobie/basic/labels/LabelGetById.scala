package bookingtour.core.doobie.basic.labels

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
final class LabelGetById private (table: String) extends GetByIdListOps[UUID, LabelE] with EnumerationToDoobieOps {

  def get(id: List[UUID]): Query0[LabelE] = {
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
      AND """) ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[LabelE.Output]
      .map(LabelE.outputTransform)
  }
}

object LabelGetById {
  final def apply(table: String): GetByIdListOps[UUID, LabelE] = new LabelGetById(table)
}
