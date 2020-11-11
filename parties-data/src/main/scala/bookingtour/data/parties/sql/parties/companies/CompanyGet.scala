package bookingtour.data.parties.sql.parties.companies

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.CompanyPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class CompanyGet private extends GetOps[UUID, CompanyPREP] with doobie.postgres.Instances {
  def get(id: UUID): query.Query0[CompanyPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            code,
            name,
            updated
           FROM
            companies
           WHERE
            mark_as_delete = FALSE
            AND id = $id""".query[CompanyPREP.Output].map(CompanyPREP.outputTransform)
  }
}

object CompanyGet {
  final def apply(): CompanyGet = new CompanyGet
}
