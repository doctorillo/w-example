package bookingtour.data.parties.sql.parties.suppliermembers

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.parties.values.SupplierGroupMemberPREP
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
final class SupplierGroupMemberCreate private (getOps: GetOps[UUID, SupplierGroupMemberPREP])
    extends ByInputOps[SupplierGroupMemberPREP.Create, SupplierGroupMemberPREP]
    with CreateOps[SupplierGroupMemberPREP.Create, SupplierGroupMemberPREP]
    with BatchCreateOps[SupplierGroupMemberPREP.Create, SupplierGroupMemberPREP] {

  def byInput(data: SupplierGroupMemberPREP.Create): query.Query0[SupplierGroupMemberPREP] = {
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
            AND group_id = ${data.groupId}
            AND supplier_id = ${data.partyId}
           ORDER BY
            id"""
      .query[SupplierGroupMemberPREP.Output]
      .map(SupplierGroupMemberPREP.outputTransform)
  }

  def insert(data: SupplierGroupMemberPREP.Create): Update0 =
    sql"""INSERT INTO provider_supplier_members ("group_id", "supplier_id") VALUES (${data.groupId}, ${data.partyId})""".update

  def runCreate(data: SupplierGroupMemberPREP.Create): ConnectionIO[SupplierGroupMemberPREP] = {
    for {
      a <- byInput(data).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                id <- insert(data)
                       .withUniqueGeneratedKeys[UUID]("id")
                c <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object SupplierGroupMemberCreate {
  final def apply(): CreateOps[SupplierGroupMemberPREP.Create, SupplierGroupMemberPREP]
    with BatchCreateOps[SupplierGroupMemberPREP.Create, SupplierGroupMemberPREP] =
    new SupplierGroupMemberCreate(SupplierGroupMemberGet())
}
