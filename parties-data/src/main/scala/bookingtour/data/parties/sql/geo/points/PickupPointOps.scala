package bookingtour.data.parties.sql.geo.points

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.PickupPointPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class PickupPointOps private extends GetAllOps[PickupPointPREP] with GetByIdListOps[UUID, PickupPointPREP] {

  def getAll(): Query0[PickupPointPREP] = {
    sql"""SELECT
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
           ORDER BY D.id"""
      .query[PickupPointPREP.Output]
      .map(PickupPointPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[PickupPointPREP] = {
    (sql"""SELECT
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
            AND
       """ ++ doobie.Fragments.in(fr"D.id", NonEmptyList.fromListUnsafe(id)))
      .query[PickupPointPREP.Output]
      .map(PickupPointPREP.outputTransform)
  }
}

object PickupPointOps {
  final def apply(): PickupPointOps = new PickupPointOps
}
