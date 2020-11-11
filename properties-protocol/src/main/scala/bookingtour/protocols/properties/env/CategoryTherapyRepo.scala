package bookingtour.protocols.properties.env

import bookingtour.protocols.core.values.api.EnumAPI
import zio.{ URIO, ZIO }

/**
 * © Alexey Toroshchin 2019.
 */
trait CategoryTherapyRepo extends Serializable {
  val categoryTherapyRepo: CategoryTherapyRepo.Service[Any]
}

object CategoryTherapyRepo {
  trait Service[R] {
    def fetchAll(): URIO[R, List[EnumAPI]]
  }

  final object > {
    def fetchAll(): URIO[CategoryTherapyRepo, List[EnumAPI]] =
      ZIO.accessM(_.categoryTherapyRepo.fetchAll())
  }
}
