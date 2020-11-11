package bookingtour.core.doobie.basic.datacoderelations

import java.util.UUID

import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.core.values.db._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class DataCodeRelationOps private (
    getAllOps: GetAllOps[DataCodeRelationE],
    getByIdListOps: GetByIdListOps[UUID, DataCodeRelationE]
) extends GetAllOps[DataCodeRelationE] with GetByIdListOps[UUID, DataCodeRelationE] {

  def getAll(): Query0[DataCodeRelationE] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[DataCodeRelationE] = getByIdListOps.get(id)
}

object DataCodeRelationOps {
  final def apply(table: String): DataCodeRelationOps =
    new DataCodeRelationOps(DataCodeRelationGetAll(table), DataCodeRelationGetById(table))
}
