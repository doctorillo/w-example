package bookingtour.data.parties.sql.parties.companies

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.CompanyPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class CompanyOps private extends GetAllOps[CompanyPREP] with GetByIdListOps[UUID, CompanyPREP] {

  def getAll(): Query0[CompanyPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            code,
            name,
            updated
           FROM
            companies
           WHERE
            mark_as_delete = FALSE""".query[CompanyPREP.Output].map(CompanyPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[CompanyPREP] = {
    (sql"""SELECT DISTINCT
            id,
            data_id,
            code,
            name,
            updated
           FROM
            companies
           WHERE
            mark_as_delete = FALSE
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[CompanyPREP.Output]
      .map(CompanyPREP.outputTransform)
  }
}

object CompanyOps {
  final def apply(): CompanyOps = new CompanyOps
}
