package bookingtour.data.parties.sql.solvers

import java.util.UUID

import bookingtour.core.doobie.modules.GetOps
import bookingtour.protocols.doobie.config.ToDoobieConfigOps
import bookingtour.protocols.parties.values.SolverPREP
import doobie.implicits._
import doobie.implicits.javatime._
import doobie.postgres.implicits._
import doobie.util.query

/**
  * © Alexey Toroshchin 2020.
  */
final class SolverGet private extends GetOps[UUID, SolverPREP] with ToDoobieConfigOps {
  def get(id: UUID): query.Query0[SolverPREP] = {
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
            AND id = $id
           ORDER BY
            id""".query[SolverPREP.Output].map(SolverPREP.outputTransform)
  }
}

object SolverGet {
  final def apply(): SolverGet = new SolverGet
}
