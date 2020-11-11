package bookingtour.data.parties.sql.parties.parties

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.PartyPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class PartyOps private extends GetAllOps[PartyPREP] with GetByIdListOps[UUID, PartyPREP] {

  def getAll(): Query0[PartyPREP] = {
    sql"""SELECT DISTINCT
            id,
            updated
           FROM
            parties
           WHERE
            mark_as_delete = FALSE""".query[PartyPREP.Output].map(PartyPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[PartyPREP] = {
    (sql"""SELECT DISTINCT
            id,
            updated
           FROM
            parties
           WHERE
            mark_as_delete = FALSE
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[PartyPREP.Output]
      .map(PartyPREP.outputTransform)
  }
}

object PartyOps {
  final def apply(): PartyOps = new PartyOps
}
