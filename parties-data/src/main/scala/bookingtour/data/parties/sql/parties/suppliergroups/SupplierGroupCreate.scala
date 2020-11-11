package bookingtour.data.parties.sql.parties.suppliergroups

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.parties.values.SupplierGroupPREP
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class SupplierGroupCreate private (getOps: GetOps[UUID, SupplierGroupPREP])
    extends ByInputOps[UUID, SupplierGroupPREP] with CreateOps[SupplierGroupPREP.Create, SupplierGroupPREP]
    with BatchCreateOps[SupplierGroupPREP.Create, SupplierGroupPREP] {

  def byInput(providerId: UUID): query.Query0[SupplierGroupPREP] = {
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
            AND data_id = $providerId
           ORDER BY
            id""".query[SupplierGroupPREP.Output].map(SupplierGroupPREP.outputTransform)
  }

  def insert(data: SupplierGroupPREP.Create): Update0 =
    sql"""INSERT INTO provider_supplier_groups ("data_id", "code", "notes") VALUES (${data.providerId}, ${data.code}, ${data.notes})""".update

  def runCreate(data: SupplierGroupPREP.Create): ConnectionIO[SupplierGroupPREP] = {
    for {
      a <- byInput(data.providerId).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object SupplierGroupCreate {
  final def apply(): CreateOps[SupplierGroupPREP.Create, SupplierGroupPREP]
    with BatchCreateOps[SupplierGroupPREP.Create, SupplierGroupPREP] =
    new SupplierGroupCreate(SupplierGroupGet())
}
