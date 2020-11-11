package bookingtour.data.parties.sql.geo.regions

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.RegionPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class RegionGet private extends GetOps[UUID, RegionPREP] {
  def get(id: UUID): query.Query0[RegionPREP] = {
    sql"""
          SELECT DISTINCT
         	id,
          country_id,
          iso_code,
          name,
         	updated
         FROM
         	regions
         WHERE
         	mark_as_delete = FALSE
          AND id = $id""".query[RegionPREP.Output].map(RegionPREP.outputTransform)
  }
}

object RegionGet {
  final def apply(): GetOps[UUID, RegionPREP] = new RegionGet
}
