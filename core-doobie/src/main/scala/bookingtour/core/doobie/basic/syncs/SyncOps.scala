package bookingtour.core.doobie.basic.syncs

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.core.values.db._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class SyncOps private (
    getAllOps: GetAllOps[SyncE],
    getByIdListOps: GetByIdListOps[UUID, SyncE]
) extends GetAllOps[SyncE] with GetByIdListOps[UUID, SyncE] {

  def getAll(): Query0[SyncE] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[SyncE] = getByIdListOps.get(id)
}

object SyncOps {
  final def apply(table: String): GetAllOps[SyncE] with GetByIdListOps[UUID, SyncE] =
    new SyncOps(SyncGetAll(table), SyncGetById(table))
}
