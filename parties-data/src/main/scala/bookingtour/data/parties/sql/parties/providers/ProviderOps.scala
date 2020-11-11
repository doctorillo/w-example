package bookingtour.data.parties.sql.parties.providers

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.ProviderPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class ProviderOps private extends GetAllOps[ProviderPREP] with GetByIdListOps[UUID, ProviderPREP] {

  def getAll(): Query0[ProviderPREP] = {
    sql"""SELECT DISTINCT
            id,
            context_id,
            party_id,
            updated
           FROM
            providers
           WHERE
            mark_as_delete = FALSE
           ORDER BY
            id""".query[ProviderPREP.Output].map(ProviderPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[ProviderPREP] = {
    (sql"""SELECT DISTINCT
            id,
            context_id,
            party_id,
            updated
           FROM
            providers
           WHERE
            mark_as_delete = FALSE
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[ProviderPREP.Output]
      .map(ProviderPREP.outputTransform)
  }
}

object ProviderOps {
  final def apply(): ProviderOps = new ProviderOps
}
