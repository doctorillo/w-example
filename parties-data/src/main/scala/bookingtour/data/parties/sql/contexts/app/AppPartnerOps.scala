package bookingtour.data.parties.sql.contexts.app

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.AppPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class AppPartnerOps private extends GetAllOps[AppPREP] with GetByIdListOps[UUID, AppPREP] {
  def getAll(): Query0[AppPREP] = {
    sql"""
         SELECT DISTINCT
         	id,
          ident,
         	updated
         FROM
         	apps
         WHERE
         	mark_as_delete = FALSE
         ORDER BY
           updated DESC
       """.query[AppPREP.Output].map(AppPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[AppPREP] = {
    (sql"""
          SELECT DISTINCT
          	id,
           ident,
          	updated
          FROM
          	apps
          WHERE
          	mark_as_delete = FALSE
            AND
       """ ++ doobie.Fragments.in(fr"id", NonEmptyList.fromListUnsafe(id)) ++
      fr"""
          ORDER BY
            updated DESC
           """).query[AppPREP.Output].map(AppPREP.outputTransform)
  }
}

object AppPartnerOps {
  final def apply(): AppPartnerOps = new AppPartnerOps
}
