package bookingtour.protocols.excursions.env

import bookingtour.protocols.core.values.api.EnumAPI
import zio.URIO

/**
  * © Alexey Toroshchin 2020.
  */
trait ExcursionTagRepo extends Serializable {
  val excursionTagRepo: ExcursionTagRepo.Service[Any]
}

object ExcursionTagRepo {
  trait Service[R] {
    def fetchAll(): URIO[R, List[EnumAPI]]
  }

  final object > {
    def fetchAll(): URIO[ExcursionTagRepo, List[EnumAPI]] =
      URIO.accessM(_.excursionTagRepo.fetchAll())
  }
}
