package bookingtour.data.parties.sql.geo.cities

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.CityPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class CityGet private extends GetOps[UUID, CityPREP] {
  def get(id: UUID): query.Query0[CityPREP] = {
    sql"""SELECT
            C.id,
            C.region_id,
            R.country_id,
            C.iso_code,
            C.name,
            C.geo_location::point,
            C.has_districts,
            C.updated
           FROM
            cities AS C
            JOIN regions AS R ON C.region_id = R.id
           WHERE
            C.mark_as_delete = FALSE
            AND C.id = $id 
           ORDER BY C.id""".query[CityPREP.Output].map(CityPREP.outputTransform)
  }
}

object CityGet {
  final def apply(): GetOps[UUID, CityPREP] = new CityGet
}
