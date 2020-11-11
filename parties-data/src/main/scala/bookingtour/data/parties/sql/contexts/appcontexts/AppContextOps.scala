package bookingtour.data.parties.sql.contexts.appcontexts

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.AppContextPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class AppContextOps private extends GetAllOps[AppContextPREP] with GetByIdListOps[UUID, AppContextPREP] {
  def getAll(): Query0[AppContextPREP] = {
    sql"""
         SELECT DISTINCT
         	id,
         	data_id,
         	value_id,
          code,
          updated
         FROM
         	app_contexts
         WHERE
          mark_as_delete = FALSE
       """.query[AppContextPREP.Output].map(AppContextPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[AppContextPREP] = {
    (sql"""
          SELECT DISTINCT
         	id,
         	data_id,
         	value_id,
          code,
          updated
         FROM
         	app_contexts
         WHERE
          mark_as_delete = FALSE
            AND
       """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[AppContextPREP.Output]
      .map(AppContextPREP.outputTransform)
  }
}

object AppContextOps {
  final def apply(): GetAllOps[AppContextPREP] with GetByIdListOps[UUID, AppContextPREP] =
    new AppContextOps
}
