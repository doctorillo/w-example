package bookingtour.data.parties.sql.parties.customergroups

import java.util.UUID

import bookingtour.core.doobie.basic.syncs.SyncCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.parties.values.CustomerGroupPREP
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
final class CustomerGroupCreate private (
    getOps: GetOps[UUID, CustomerGroupPREP],
    syncOps: CreateOps[SyncE.Create, SyncE] with ByInputOps[SyncItem, SyncE]
) extends ByInputOps[CustomerGroupPREP.Create, CustomerGroupPREP]
    with CreateOps[CustomerGroupPREP.Create, CustomerGroupPREP]
    with BatchCreateOps[CustomerGroupPREP.Create, CustomerGroupPREP] {

  def byInput(data: CustomerGroupPREP.Create): query.Query0[CustomerGroupPREP] = {
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
            AND data_id = ${data.providerId}
            AND code = ${data.code}
           ORDER BY
            id"""
      .query[CustomerGroupPREP.Output]
      .map(CustomerGroupPREP.outputTransform)
  }

  def insert(data: CustomerGroupPREP.Create): Update0 =
    sql"""INSERT INTO provider_customer_groups ("data_id", "code", "notes") VALUES (${data.providerId}, ${data.code}, ${data.notes})""".update

  private def createWithSync(
      data: CustomerGroupPREP.Create,
      sync: SyncItem
  ): ConnectionIO[CustomerGroupPREP] = {
    for {
      a <- syncOps.byInput(sync).option
      b <- a match {
            case Some(value) =>
              getOps.get(value.dataId).unique

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                _  <- syncOps.runCreate(SyncE.Create(dataId = id, sync = sync))
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }

  private def create(data: CustomerGroupPREP.Create): ConnectionIO[CustomerGroupPREP] = {
    for {
      a <- byInput(data).option
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

  def runCreate(data: CustomerGroupPREP.Create): ConnectionIO[CustomerGroupPREP] =
    data.sync.fold(create(data))(x => createWithSync(data, x))
}

object CustomerGroupCreate {
  final def apply(tableSync: String): CustomerGroupCreate =
    new CustomerGroupCreate(CustomerGroupGet(), SyncCreate(tableSync))
}
