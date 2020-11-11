package bookingtour.data.parties.sql.geo.countries

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.CountryPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class CountryGet private extends GetOps[UUID, CountryPREP] {
  def get(id: UUID): query.Query0[CountryPREP] = {
    sql"""SELECT DISTINCT
         	"id",
          iso_code_2,
          iso_code_3,
          "name",
          updated
         FROM
         	countries
         WHERE
         	mark_as_delete = FALSE
          AND id = $id 
         ORDER BY "name""""
      .query[CountryPREP.Output]
      .map(CountryPREP.outputTransform)
  }
}

object CountryGet {
  final def apply(): GetOps[UUID, CountryPREP] = new CountryGet
}
