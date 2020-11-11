package bookingtour.data.parties.sql.geo.points

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.PickupPointPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class PickupPointGetOps private extends GetOps[UUID, PickupPointPREP] {
  def get(id: UUID): query.Query0[PickupPointPREP] = {
    sql"""
          SELECT
         	D.id,
          C.id,
          C.region_id,
          R.country_id,
         	D.name,
          D.type_id,
          D.geo_location,
         	D.updated
         FROM
         	city_points AS D
         	JOIN cities AS C ON D.city_id = C.id
          JOIN regions AS R ON C.region_id = R.id
         WHERE
         	D.mark_as_delete = FALSE
          AND D.id = $id 
         ORDER BY D.id""".query[PickupPointPREP.Output].map(PickupPointPREP.outputTransform)
  }
}

object PickupPointGetOps {
  final def apply(): PickupPointGetOps = new PickupPointGetOps
}
