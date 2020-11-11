package bookingtour.data.parties.sql.parties.suppliergroups

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.SupplierGroupPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class SupplierGroupOps private extends GetAllOps[SupplierGroupPREP] with GetByIdListOps[UUID, SupplierGroupPREP] {

  def getAll(): Query0[SupplierGroupPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            code,
            notes,
            updated
           FROM
            provider_supplier_groups
           WHERE
            mark_as_delete = FALSE
           ORDER BY
            id""".query[SupplierGroupPREP.Output].map(SupplierGroupPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[SupplierGroupPREP] = {
    (sql"""SELECT DISTINCT
            id,
            data_id,
            code,
            notes,
            updated
           FROM
            provider_supplier_groups
           WHERE
            mark_as_delete = FALSE
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[SupplierGroupPREP.Output]
      .map(SupplierGroupPREP.outputTransform)
  }
}

object SupplierGroupOps {
  final def apply(): SupplierGroupOps = new SupplierGroupOps
}
