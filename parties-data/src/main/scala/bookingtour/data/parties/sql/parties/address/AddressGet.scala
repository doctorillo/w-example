package bookingtour.data.parties.sql.parties.address

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.AddressPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class AddressGet private extends GetOps[UUID, AddressPREP] {
  def get(id: UUID): query.Query0[AddressPREP] = {
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
            AND A.id = $id""".query[AddressPREP.Output].map(AddressPREP.outputTransform)
  }
}

object AddressGet {
  final def apply(): AddressGet = new AddressGet
}
