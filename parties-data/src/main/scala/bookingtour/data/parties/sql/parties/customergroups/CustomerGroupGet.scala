package bookingtour.data.parties.sql.parties.customergroups

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.CustomerGroupPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class CustomerGroupGet private extends GetOps[UUID, CustomerGroupPREP] {
  def get(id: UUID): Query0[CustomerGroupPREP] = {
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
            AND id = $id
           ORDER BY
            id""".query[CustomerGroupPREP.Output].map(CustomerGroupPREP.outputTransform)
  }
}

object CustomerGroupGet {
  final def apply(): CustomerGroupGet = new CustomerGroupGet
}
