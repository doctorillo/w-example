package bookingtour.data.parties.sql.parties.address

import java.util.UUID

import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, CreateOps, GetOps}
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import bookingtour.protocols.parties.values.AddressPREP
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.query.Query0
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class AddressCreate private (getOps: GetOps[UUID, AddressPREP])
    extends ByInputOps[AddressPREP.Create, AddressPREP] with CreateOps[AddressPREP.Create, AddressPREP]
    with BatchCreateOps[AddressPREP.Create, AddressPREP] with EnumerationToDoobieOps {

  def byInput(data: AddressPREP.Create): Query0[AddressPREP] = {
    sql"""SELECT
            A.id,
            A.city_id,
            C.region_id,
            R.country_id,
            A.district_id,
            A.party_id,
            A.address,
            A.street_name,
            A.internal,
            A.location,
            A.zip,
            A.geo_location::point,
            A.updated
           FROM
            addresses AS A
            JOIN cities AS C ON A.city_id = C.id
            JOIN regions AS R ON C.region_id = R.id
           WHERE
            A.mark_as_delete = FALSE
            AND A.party_id = ${data.partyId}
            AND A.address = ${data.category}"""
      .query[AddressPREP.Output]
      .map(AddressPREP.outputTransform)
  }

  def insert(data: AddressPREP.Create): Update0 =
    sql"""INSERT INTO addresses ("city_id", "party_id", "address", "street_name", "zip") VALUES (${data.cityId}, ${data.partyId}, ${data.category}, ${data.street}, ${data.zip})""".update

  def runCreate(data: AddressPREP.Create): ConnectionIO[AddressPREP] = {
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

object AddressCreate {
  final def apply(): CreateOps[AddressPREP.Create, AddressPREP] with BatchCreateOps[AddressPREP.Create, AddressPREP] =
    new AddressCreate(AddressGet())
}
