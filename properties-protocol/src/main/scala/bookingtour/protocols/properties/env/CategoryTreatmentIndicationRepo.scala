package bookingtour.protocols.properties.env

import bookingtour.protocols.core.values.api.EnumAPI
import zio.{ URIO, ZIO }

/**
 * © Alexey Toroshchin 2019.
 */
trait CategoryTreatmentIndicationRepo extends Serializable {
  val categoryTreatmentIndicationRepo: CategoryTreatmentIndicationRepo.Service[Any]
}

object CategoryTreatmentIndicationRepo {
  trait Service[R] {
    def fetchAll(): URIO[R, List[EnumAPI]]
  }

  final object > {
    def fetchAll(): URIO[CategoryTreatmentIndicationRepo, List[EnumAPI]] =
      ZIO.accessM(_.categoryTreatmentIndicationRepo.fetchAll())
  }
}
