package bookingtour.data.parties.sql.contexts.app

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.AppPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class AppPartnerGet private extends GetOps[UUID, AppPREP] {
  def get(id: UUID): query.Query0[AppPREP] = {
    sql"""
         |SELECT DISTINCT
         |	id,
         |  ident,
         |  updated
         |FROM
         |	apps
         |WHERE
         |  id = $id
         |	AND mark_as_delete = FALSE
         |""".stripMargin.query[AppPREP.Output].map(AppPREP.outputTransform)
  }
}

object AppPartnerGet {
  final def apply(): GetOps[UUID, AppPREP] = new AppPartnerGet
}
