package bookingtour.data.parties.sql.users

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.parties.values.UserPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class UserGet private extends GetOps[UUID, UserPREP] {
  def get(id: UUID): query.Query0[UserPREP] = {
    sql"""SELECT DISTINCT
            id,
            solver_ref_id,
            app_id,
            business_party_id,
            roles,
            updated
           FROM
            users
           WHERE
            mark_as_delete = FALSE
            AND id = $id
           ORDER BY
            id""".query[UserPREP.Output].map(UserPREP.outputTransform)
  }
}

object UserGet {
  final def apply(): UserGet = new UserGet
}
