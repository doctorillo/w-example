package bookingtour.core.doobie.basic.images

import java.util.UUID

import bookingtour.core.doobie.modules.GetByIdListOps
import bookingtour.core.doobie.modules.{GetAllOps, GetByIdListOps}
import bookingtour.protocols.core.values.db._
import bookingtour.protocols.doobie.config.EnumerationToDoobieOps
import doobie.util.query.Query0

/**
  * © Alexey Toroshchin 2020.
  */
final class ImageOps private (
    getAllOps: GetAllOps[ImageE],
    getByIdOps: GetByIdListOps[UUID, ImageE]
) extends GetAllOps[ImageE] with GetByIdListOps[UUID, ImageE] with EnumerationToDoobieOps {

  def getAll(): Query0[ImageE] = getAllOps.getAll()

  def get(id: List[UUID]): Query0[ImageE] = getByIdOps.get(id)
}

object ImageOps {
  final def apply(table: String): GetAllOps[ImageE] with GetByIdListOps[UUID, ImageE] =
    new ImageOps(ImageGetAll(table), ImageGetById(table))
}
