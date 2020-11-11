package bookingtour.data.parties.sql.geo.districts

import java.util.UUID

import bookingtour.core.doobie.basic.labels.LabelCreate
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.core.doobie.modules.{BatchCreateOps, ByInputOps, CreateOps, GetOps}
import bookingtour.protocols.core.values.db.LabelE
import bookingtour.protocols.parties.values.CityDistrictPREP
import cats.syntax.applicative._
import doobie.ConnectionIO
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0
import doobie.util.update.Update0

/**
  * © Alexey Toroshchin 2020.
  */
final class DistrictCreateOps private (
    getOps: GetOps[UUID, CityDistrictPREP],
    labelOps: CreateOps[LabelE.Create, LabelE] with BatchCreateOps[LabelE.Create, LabelE]
) extends ByInputOps[CityDistrictPREP.Create, CityDistrictPREP]
    with CreateOps[CityDistrictPREP.Create, CityDistrictPREP]
    with BatchCreateOps[CityDistrictPREP.Create, CityDistrictPREP] {

  def byInput(data: CityDistrictPREP.Create): Query0[CityDistrictPREP] = {
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
          AND C.id = ${data.cityId} 
          AND D.name = ${data.name} 
         ORDER BY D.id""".query[CityDistrictPREP.Output].map(CityDistrictPREP.outputTransform)
  }

  def insert(data: CityDistrictPREP.Create): Update0 =
    sql"""INSERT INTO city_districts ("city_id", "name") VALUES (${data.cityId}, ${data.name})""".update

  def runCreate(data: CityDistrictPREP.Create): ConnectionIO[CityDistrictPREP] = {
    for {
      a <- byInput(data).option
      b <- a match {
            case Some(value) =>
              value.pure[ConnectionIO]

            case None =>
              for {
                id <- insert(data).withUniqueGeneratedKeys[UUID]("id")
                _  <- labelOps.runCreateList(LabelE.create(id, data.name))
                c  <- getOps.get(id).unique
              } yield c
          }
    } yield b
  }
}

object DistrictCreateOps {
  final def apply(labelOps: LabelCreate): DistrictCreateOps =
    new DistrictCreateOps(DistrictGetOps(), labelOps)
}
