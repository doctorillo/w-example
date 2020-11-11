package bookingtour.data.parties.sql.parties.customermembers

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.doobie.config.ToDoobieConfigOps
import bookingtour.protocols.parties.values.CustomerGroupMemberPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class CustomerGroupMemberGet private extends GetOps[UUID, CustomerGroupMemberPREP] with ToDoobieConfigOps {
  def get(id: UUID): query.Query0[CustomerGroupMemberPREP] = {
    sql"""SELECT DISTINCT
            id,
            group_id,
            customer_id,
            lower(dates_system_life),
            upper(dates_system_life),
            updated
          FROM
            provider_customer_members
          WHERE
            mark_as_delete = FALSE
            AND id = $id
           ORDER BY
            id""".query[CustomerGroupMemberPREP.Output].map(CustomerGroupMemberPREP.outputTransform)
  }
}

object CustomerGroupMemberGet {
  final def apply(): CustomerGroupMemberGet = new CustomerGroupMemberGet
}
