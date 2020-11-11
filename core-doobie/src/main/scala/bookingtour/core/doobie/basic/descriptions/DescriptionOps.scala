package bookingtour.core.doobie.basic.descriptions

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.core.values.db._
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class DescriptionOps private (
    getAllOps: GetAllOps[DescriptionE],
    getByIdListOps: GetByIdListOps[UUID, DescriptionE]
) extends GetAllOps[DescriptionE] with GetByIdListOps[UUID, DescriptionE] with EnumerationToDoobieOps {

  def getAll(): Query0[DescriptionE] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[DescriptionE] = getByIdListOps.get(id)
}

object DescriptionOps {
  final def apply(table: String): DescriptionOps =
    new DescriptionOps(DescriptionGetAll(table), DescriptionGetById(table))
}
