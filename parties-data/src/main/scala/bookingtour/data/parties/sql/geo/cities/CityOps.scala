package bookingtour.data.parties.sql.geo.cities

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.CityPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class CityOps private extends GetAllOps[CityPREP] with GetByIdListOps[UUID, CityPREP] {
  def getAll(): Query0[CityPREP] = {
    sql"""
         SELECT
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
         ORDER BY
           C.id
       """.query[CityPREP.Output].map(CityPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[CityPREP] = {
    (sql"""
          SELECT
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
            AND
       """ ++ doobie.Fragments.in(fr"C.id", NonEmptyList.fromListUnsafe(id)) ++
      fr"""
          ORDER BY
           C.id
           """).query[CityPREP.Output].map(CityPREP.outputTransform)
  }
}

object CityOps {
  final def apply(): CityOps = new CityOps
}
