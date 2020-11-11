package bookingtour.protocols.properties.env

import bookingtour.protocols.core.values.api.EnumAPI
import zio.{ URIO, ZIO }

/**
 * © Alexey Toroshchin 2019.
 */
trait CategoryMedicalDepartmentRepo extends Serializable {
  val categoryMedicalDepartmentRepo: CategoryMedicalDepartmentRepo.Service[Any]
}

object CategoryMedicalDepartmentRepo {
  trait Service[R] {
    def fetchAll(): URIO[R, List[EnumAPI]]
  }

  final object > {
    def fetchAll(): URIO[CategoryMedicalDepartmentRepo, List[EnumAPI]] =
      ZIO.accessM(_.categoryMedicalDepartmentRepo.fetchAll())
  }
}
