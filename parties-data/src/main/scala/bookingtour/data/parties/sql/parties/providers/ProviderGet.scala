package bookingtour.data.parties.sql.parties.providers

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.ProviderPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class ProviderGet private extends GetOps[UUID, ProviderPREP] {
  def get(id: UUID): query.Query0[ProviderPREP] = {
    sql"""SELECT DISTINCT
            id,
            context_id,
            party_id,
            updated
           FROM
            providers
           WHERE
            mark_as_delete = FALSE
            AND id = $id
           ORDER BY
            id""".query[ProviderPREP.Output].map(ProviderPREP.outputTransform)
  }
}

object ProviderGet {
  final def apply(): ProviderGet = new ProviderGet
}
