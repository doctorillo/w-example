package bookingtour.core.doobie.basic.datavaluecoderelations

import java.util.UUID

import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.core.values.db._
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class DataValueCodeRelationOps private (
    getAllOps: GetAllOps[DataValueCodeRelationE],
    getByIdListOps: GetByIdListOps[UUID, DataValueCodeRelationE]
) extends GetAllOps[DataValueCodeRelationE] with GetByIdListOps[UUID, DataValueCodeRelationE] {

  def getAll(): Query0[DataValueCodeRelationE] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[DataValueCodeRelationE] = getByIdListOps.get(id)
}

object DataValueCodeRelationOps {
  final def apply(table: String): DataValueCodeRelationOps =
    new DataValueCodeRelationOps(
      DataValueCodeRelationGetAll(table),
      DataValueCodeRelationGetById(table)
    )
}
