package bookingtour.data.parties.sql.geo.districts

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.CityDistrictPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class DistrictOps private extends GetAllOps[CityDistrictPREP] with GetByIdListOps[UUID, CityDistrictPREP] {
  def getAll(): Query0[CityDistrictPREP] = {
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
         ORDER BY
          D.id
       """.query[CityDistrictPREP.Output].map(CityDistrictPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[CityDistrictPREP] = {
    (sql"""
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
            AND
       """ ++ doobie.Fragments.in(fr"D.id", NonEmptyList.fromListUnsafe(id)) ++
      fr"""ORDER BY D.id""").query[CityDistrictPREP.Output].map(CityDistrictPREP.outputTransform)
  }
}

object DistrictOps {
  final def apply(): DistrictOps = new DistrictOps
}
