package bookingtour.protocols.properties.env

import bookingtour.protocols.core.values.api.EnumAPI
import zio.{ URIO, ZIO }

/**
 * © Alexey Toroshchin 2019.
 */
trait CategoryAmenityRepo extends Serializable {
  val categoryAmenityRepo: CategoryAmenityRepo.Service[Any]
}

object CategoryAmenityRepo {
  trait Service[R] {
    def fetchAll(): URIO[R, List[EnumAPI]]
  }

  final object > {
    def fetchAll(): URIO[CategoryAmenityRepo, List[EnumAPI]] =
      ZIO.accessM(_.categoryAmenityRepo.fetchAll())
  }
}
