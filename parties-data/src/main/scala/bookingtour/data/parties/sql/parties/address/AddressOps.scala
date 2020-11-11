package bookingtour.data.parties.sql.parties.address

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.AddressPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class AddressOps private extends GetAllOps[AddressPREP] with GetByIdListOps[UUID, AddressPREP] {

  def getAll(): Query0[AddressPREP] = {
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
            A.mark_as_delete = FALSE""".query[AddressPREP.Output].map(AddressPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[AddressPREP] = {
    (sql"""
          SELECT
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
            AND
       """ ++ doobie.Fragments.in(fr"A.id", NonEmptyList.fromListUnsafe(id)))
      .query[AddressPREP.Output]
      .map(AddressPREP.outputTransform)
  }
}

object AddressOps {
  final def apply(): AddressOps = new AddressOps
}
