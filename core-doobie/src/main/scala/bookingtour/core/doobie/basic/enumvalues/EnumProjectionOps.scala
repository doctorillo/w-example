package bookingtour.core.doobie.basic.enumvalues

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.core.values.db._
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class EnumProjectionOps private (
    getAllOps: GetAllOps[EnumProjectionE],
    getByIdListOps: GetByIdListOps[UUID, EnumProjectionE]
) extends GetAllOps[EnumProjectionE] with GetByIdListOps[UUID, EnumProjectionE] with EnumerationToDoobieOps {

  def getAll(): Query0[EnumProjectionE] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[EnumProjectionE] = getByIdListOps.get(id)
}

object EnumProjectionOps {
  final def apply(table: String): GetAllOps[EnumProjectionE] with GetByIdListOps[UUID, EnumProjectionE] =
    new EnumProjectionOps(EnumProjectionGetAll(table), EnumProjectionGetById(table))
}
