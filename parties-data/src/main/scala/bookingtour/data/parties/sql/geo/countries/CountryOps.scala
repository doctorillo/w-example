package bookingtour.data.parties.sql.geo.countries

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.CountryPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class CountryOps private extends GetAllOps[CountryPREP] with GetByIdListOps[UUID, CountryPREP] {

  def getAll(): Query0[CountryPREP] = {
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
         ORDER BY
          id
       """.query[CountryPREP.Output].map(CountryPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[CountryPREP] = {
    (sql"""SELECT DISTINCT
            "id",
            iso_code_2,
            iso_code_3,
            "name",
            updated
           FROM
            countries
           WHERE
            mark_as_delete = FALSE
            AND """ ++ doobie.Fragments.in(fr"id", NonEmptyList.fromListUnsafe(id)) ++
      fr"""ORDER BY id""").query[CountryPREP.Output].map(CountryPREP.outputTransform)
  }
}

object CountryOps {
  final def apply(): CountryOps = new CountryOps
}
