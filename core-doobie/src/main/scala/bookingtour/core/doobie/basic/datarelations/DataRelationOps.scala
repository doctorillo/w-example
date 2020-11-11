package bookingtour.core.doobie.basic.datarelations

import java.util.UUID

import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.core.values.db._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class DataRelationOps private (
    getAllOps: GetAllOps[DataRelationE],
    getByIdListOps: GetByIdListOps[UUID, DataRelationE]
) extends GetAllOps[DataRelationE] with GetByIdListOps[UUID, DataRelationE] {

  def getAll(): Query0[DataRelationE] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[DataRelationE] = getByIdListOps.get(id)
}

object DataRelationOps {
  final def apply(table: String): DataRelationOps =
    new DataRelationOps(DataRelationGetAll(table), DataRelationGetById(table))
}
