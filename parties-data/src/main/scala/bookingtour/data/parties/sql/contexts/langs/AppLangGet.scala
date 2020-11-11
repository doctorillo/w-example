package bookingtour.data.parties.sql.contexts.langs

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.AppLangPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class AppLangGet private extends GetOps[UUID, AppLangPREP] {
  def get(id: UUID): query.Query0[AppLangPREP] = {
    sql"""
         SELECT DISTINCT
         	id,
         	data_id,
         	lang_id,
          active,
          updated
         FROM
         	app_langs
         WHERE
           id = $id
         """
      .query[AppLangPREP.Output]
      .map(AppLangPREP.outputTransform)
  }
}

object AppLangGet {
  final def apply(): GetOps[UUID, AppLangPREP] = new AppLangGet
}
