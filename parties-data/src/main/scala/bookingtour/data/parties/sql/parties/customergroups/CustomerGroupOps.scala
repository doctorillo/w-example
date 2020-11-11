package bookingtour.data.parties.sql.parties.customergroups

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.CustomerGroupPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class CustomerGroupOps private extends GetAllOps[CustomerGroupPREP] with GetByIdListOps[UUID, CustomerGroupPREP] {

  def getAll(): Query0[CustomerGroupPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            code,
            notes,
            updated
           FROM
            provider_customer_groups
           WHERE
            mark_as_delete = FALSE
           ORDER BY
            id""".query[CustomerGroupPREP.Output].map(CustomerGroupPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[CustomerGroupPREP] = {
    (sql"""SELECT DISTINCT
            id,
            data_id,
            code,
            notes,
            updated
           FROM
            provider_customer_groups
           WHERE
            mark_as_delete = FALSE
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[CustomerGroupPREP.Output]
      .map(CustomerGroupPREP.outputTransform)
  }
}

object CustomerGroupOps {
  final def apply(): CustomerGroupOps = new CustomerGroupOps
}
