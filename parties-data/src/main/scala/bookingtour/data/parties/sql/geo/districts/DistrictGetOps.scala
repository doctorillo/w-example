package bookingtour.data.parties.sql.geo.districts

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.CityDistrictPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class DistrictGetOps private extends GetOps[UUID, CityDistrictPREP] {
  def get(id: UUID): query.Query0[CityDistrictPREP] = {
    sql"""
          SELECT DISTINCT
         	D.id,
          C.id,
          C.region_id,
          R.country_id,
         	D.name,
         	D.updated
         FROM
         	city_districts AS D
         	JOIN cities AS C ON D.city_id = C.id
          JOIN regions AS R ON C.region_id = R.id
         WHERE
         	D.mark_as_delete = FALSE
          AND D.id = $id 
         ORDER BY D.id""".query[CityDistrictPREP.Output].map(CityDistrictPREP.outputTransform)
  }
}

object DistrictGetOps {
  final def apply(): DistrictGetOps = new DistrictGetOps
}
