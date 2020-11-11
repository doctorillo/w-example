package bookingtour.data.parties.sql.parties.suppliergroups

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.SupplierGroupPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class SupplierGroupGet private extends GetOps[UUID, SupplierGroupPREP] {
  def get(id: UUID): Query0[SupplierGroupPREP] = {
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
            AND id = $id
           ORDER BY
            id""".query[SupplierGroupPREP.Output].map(SupplierGroupPREP.outputTransform)
  }
}

object SupplierGroupGet {
  final def apply(): SupplierGroupGet = new SupplierGroupGet
}
