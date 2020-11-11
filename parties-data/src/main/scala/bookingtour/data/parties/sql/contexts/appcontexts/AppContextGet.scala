package bookingtour.data.parties.sql.contexts.appcontexts

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.AppContextPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class AppContextGet private extends GetOps[UUID, AppContextPREP] {
  def get(id: UUID): query.Query0[AppContextPREP] = {
    sql"""SELECT DISTINCT
         	id,
         	data_id,
         	value_id,
          code,
          updated
         FROM
         	app_contexts
         WHERE
           id = $id
         	 AND mark_as_delete = FALSE
         """
      .query[AppContextPREP.Output]
      .map(AppContextPREP.outputTransform)
  }
}

object AppContextGet {
  final def apply(): GetOps[UUID, AppContextPREP] = new AppContextGet
}
