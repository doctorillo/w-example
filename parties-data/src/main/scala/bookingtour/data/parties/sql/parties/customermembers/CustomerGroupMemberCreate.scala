package bookingtour.data.parties.sql.parties.customermembers

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.parties.values.CustomerGroupMemberPREP
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
final class CustomerGroupMemberCreate private (getOps: GetOps[UUID, CustomerGroupMemberPREP])
    extends ByInputOps[CustomerGroupMemberPREP.Create, CustomerGroupMemberPREP]
    with CreateOps[CustomerGroupMemberPREP.Create, CustomerGroupMemberPREP]
    with BatchCreateOps[CustomerGroupMemberPREP.Create, CustomerGroupMemberPREP] {

  def byInput(data: CustomerGroupMemberPREP.Create): query.Query0[CustomerGroupMemberPREP] = {
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
            AND group_id = ${data.groupId}
            AND customer_id = ${data.partyId}
           ORDER BY
            id"""
      .query[CustomerGroupMemberPREP.Output]
      .map(CustomerGroupMemberPREP.outputTransform)
  }

  def insert(data: CustomerGroupMemberPREP.Create): Update0 =
    sql"""INSERT INTO provider_customer_members ("group_id", "customer_id") VALUES (${data.groupId}, ${data.partyId})""".update

  def runCreate(data: CustomerGroupMemberPREP.Create): ConnectionIO[CustomerGroupMemberPREP] = {
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
}

object CustomerGroupMemberCreate {
  final def apply(): CustomerGroupMemberCreate =
    new CustomerGroupMemberCreate(CustomerGroupMemberGet())
}
