package bookingtour.core.doobie.basic.labels

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.core.values.db._
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class LabelOps private (
    getAllOps: GetAllOps[LabelE],
    getByIdListOps: GetByIdListOps[UUID, LabelE]
) extends GetAllOps[LabelE] with GetByIdListOps[UUID, LabelE] with EnumerationToDoobieOps {

  def getAll(): Query0[LabelE] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[LabelE] = getByIdListOps.get(id)
}

object LabelOps {
  final def apply(table: String): GetAllOps[LabelE] with GetByIdListOps[UUID, LabelE] =
    new LabelOps(LabelGetAll(table), LabelGetById(table))
}
