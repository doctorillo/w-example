package bookingtour.data.parties.sql.users

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.UserPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class UserOps private extends GetAllOps[UserPREP] with GetByIdListOps[UUID, UserPREP] {

  def getAll(): Query0[UserPREP] = {
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
           ORDER BY
            id""".query[UserPREP.Output].map(UserPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[UserPREP] = {
    (sql"""SELECT DISTINCT
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
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[UserPREP.Output]
      .map(UserPREP.outputTransform)
  }
}

object UserOps {
  final def apply(): UserOps = new UserOps
}
