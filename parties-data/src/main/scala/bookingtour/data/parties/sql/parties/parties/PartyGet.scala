package bookingtour.data.parties.sql.parties.parties

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.PartyPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class PartyGet private extends GetOps[UUID, PartyPREP] {
  def get(id: UUID): query.Query0[PartyPREP] = {
    sql"""SELECT DISTINCT
            id,
            updated
           FROM
            parties
           WHERE
            mark_as_delete = FALSE
            AND id = $id""".query[PartyPREP.Output].map(PartyPREP.outputTransform)
  }
}

object PartyGet {
  final def apply(): PartyGet = new PartyGet
}
