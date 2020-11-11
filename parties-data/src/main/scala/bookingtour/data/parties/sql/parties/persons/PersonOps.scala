package bookingtour.data.parties.sql.parties.persons

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.PersonPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class PersonOps private extends GetAllOps[PersonPREP] with GetByIdListOps[UUID, PersonPREP] {

  def getAll(): Query0[PersonPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            first_name,
            last_name,
            updated
           FROM
            persons
           WHERE
            mark_as_delete = FALSE""".query[PersonPREP.Output].map(PersonPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[PersonPREP] = {
    (sql"""SELECT DISTINCT
            id,
            data_id,
            first_name,
            last_name,
            updated
           FROM
            persons
           WHERE
            mark_as_delete = FALSE
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[PersonPREP.Output]
      .map(PersonPREP.outputTransform)
  }
}

object PersonOps {
  final def apply(): PersonOps = new PersonOps
}
