package bookingtour.data.parties.sql.solvers

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.parties.values.SolverPREP
import cats.data.NonEmptyList
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.fragments.in
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class SolverOps private extends GetAllOps[SolverPREP] with GetByIdListOps[UUID, SolverPREP] {

  def getAll(): Query0[SolverPREP] = {
    sql"""SELECT DISTINCT
            id,
            data_id,
            email,
            password_hash,
            preferred_lang,
            updated
           FROM
            solvers
           WHERE
            mark_as_delete = FALSE
           ORDER BY
            id""".query[SolverPREP.Output].map(SolverPREP.outputTransform)
  }

  def get(id: List[UUID]): Query0[SolverPREP] = {
    (sql"""SELECT DISTINCT
            id,
            data_id,
            email,
            password_hash,
            preferred_lang,
            updated
           FROM
            solvers
           WHERE
            mark_as_delete = FALSE
            AND """ ++ in(fr"id", NonEmptyList.fromListUnsafe(id)))
      .query[SolverPREP.Output]
      .map(SolverPREP.outputTransform)
  }
}

object SolverOps {
  final def apply(): SolverOps = new SolverOps
}
