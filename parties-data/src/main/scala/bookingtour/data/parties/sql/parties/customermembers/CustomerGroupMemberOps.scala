package bookingtour.data.parties.sql.parties.customermembers

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.CustomerGroupMemberPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class CustomerGroupMemberOps private
    extends GetAllOps[CustomerGroupMemberPREP] with GetByIdListOps[UUID, CustomerGroupMemberPREP] {

  def getAll(): Query0[CustomerGroupMemberPREP] = {
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
          ORDER BY
          id"""
      .query[CustomerGroupMemberPREP.Output]
      .map(CustomerGroupMemberPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[CustomerGroupMemberPREP] = {
    (sql"""SELECT DISTINCT
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
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[CustomerGroupMemberPREP.Output]
      .map(CustomerGroupMemberPREP.outputTransform)
  }
}

object CustomerGroupMemberOps {
  final def apply(): CustomerGroupMemberOps = new CustomerGroupMemberOps
}
