package bookingtour.data.parties.sql.contexts.langs

import bookingtour.core.doobie.modules.GetAllOps
import bookingtour.protocols.parties.values.AppLangPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class AppLangGetAll private extends GetAllOps[AppLangPREP] {

  def getAll(): Query0[AppLangPREP] = {
    sql"""SELECT DISTINCT
         	id,
         	data_id,
         	lang_id,
          active,
          updated
         FROM
         	app_langs
         ORDER BY
            id"""
      .query[AppLangPREP.Output]
      .map(AppLangPREP.outputTransform)
  }
}

object AppLangGetAll {
  final def apply(): GetAllOps[AppLangPREP] = new AppLangGetAll
}
