package bookingtour.data.parties.sql.geo.regions

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.RegionPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class RegionOps private extends GetAllOps[RegionPREP] with GetByIdListOps[UUID, RegionPREP] {

  def getAll(): Query0[RegionPREP] = {
    sql"""SELECT DISTINCT
            id,
            country_id,
            iso_code,
            name,
            updated
           FROM
            regions
           WHERE
            mark_as_delete = FALSE""".query[RegionPREP.Output].map(RegionPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[RegionPREP] = {
    (sql"""
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
            AND
       """ ++ doobie.Fragments.in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[RegionPREP.Output]
      .map(RegionPREP.outputTransform)
  }
}

object RegionOps {
  final def apply(): RegionOps = new RegionOps
}
