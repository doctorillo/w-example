package bookingtour.protocols.properties.env

import bookingtour.protocols.core.values.api.EnumAPI
import zio.{ URIO, ZIO }

/**
 * © Alexey Toroshchin 2019.
 */
trait CategoryRoomFacilityRepo extends Serializable {
  val categoryRoomFacilityRepo: CategoryRoomFacilityRepo.Service[Any]
}

object CategoryRoomFacilityRepo {
  trait Service[R] {
    def fetchAll(): URIO[R, List[EnumAPI]]
  }

  final object > {
    def fetchAll(): URIO[CategoryRoomFacilityRepo, List[EnumAPI]] =
      ZIO.accessM(_.categoryRoomFacilityRepo.fetchAll())
  }
}
