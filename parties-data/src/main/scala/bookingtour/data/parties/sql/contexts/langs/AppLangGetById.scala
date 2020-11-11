package bookingtour.data.parties.sql.contexts.langs

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.protocols.parties.values.AppLangPREP
import cats.data.NonEmptyList
import doobie.Fragments.in
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class AppLangGetById private extends GetByIdListOps[UUID, AppLangPREP] {

  def get(id: List[UUID]): Query0[AppLangPREP] = {
    (sql"""SELECT DISTINCT
         	id,
         	data_id,
         	lang_id,
          active,
          updated
         FROM
         	app_langs
         WHERE
            mark_as_delete = FALSE
      AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[AppLangPREP.Output]
      .map(AppLangPREP.outputTransform)
  }
}

object AppLangGetById {
  final def apply(): GetByIdListOps[UUID, AppLangPREP] =
    new AppLangGetById
}
