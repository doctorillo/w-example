package bookingtour.data.parties.sql.parties.suppliermembers

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.doobie.config.ToDoobieConfigOps
import bookingtour.protocols.parties.values.SupplierGroupMemberPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class SupplierGroupMemberGet private extends GetOps[UUID, SupplierGroupMemberPREP] with ToDoobieConfigOps {
  def get(id: UUID): query.Query0[SupplierGroupMemberPREP] = {
    sql"""SELECT DISTINCT
            id,
            group_id,
            supplier_id,
            lower(dates_system_life),
            upper(dates_system_life),
            updated
           FROM
            provider_supplier_members
           WHERE
            mark_as_delete = FALSE
            AND id = $id
           ORDER BY
            id""".query[SupplierGroupMemberPREP.Output].map(SupplierGroupMemberPREP.outputTransform)
  }
}

object SupplierGroupMemberGet {
  final def apply(): SupplierGroupMemberGet = new SupplierGroupMemberGet
}
