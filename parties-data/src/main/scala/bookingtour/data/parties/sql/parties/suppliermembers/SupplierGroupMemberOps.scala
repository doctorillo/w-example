package bookingtour.data.parties.sql.parties.suppliermembers

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.SupplierGroupMemberPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class SupplierGroupMemberOps private
    extends GetAllOps[SupplierGroupMemberPREP] with GetByIdListOps[UUID, SupplierGroupMemberPREP] {

  def getAll(): Query0[SupplierGroupMemberPREP] = {
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
           ORDER BY
            id""".query[SupplierGroupMemberPREP.Output].map(SupplierGroupMemberPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[SupplierGroupMemberPREP] = {
    (sql"""SELECT DISTINCT
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
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[SupplierGroupMemberPREP.Output]
      .map(SupplierGroupMemberPREP.outputTransform)
  }
}

object SupplierGroupMemberOps {
  final def apply(): SupplierGroupMemberOps = new SupplierGroupMemberOps
}
