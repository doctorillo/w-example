package bookingtour.data.parties.sql.parties.persons

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.PersonPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class PersonGet private extends GetOps[UUID, PersonPREP] {
  def get(id: UUID): query.Query0[PersonPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            first_name,
            last_name,
            updated
           FROM
            persons
           WHERE
            mark_as_delete = FALSE
            AND id = $id""".query[PersonPREP.Output].map(PersonPREP.outputTransform)
  }
}

object PersonGet {
  final def apply(): PersonGet = new PersonGet
}
