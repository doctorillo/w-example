package bookingtour.protocols.orders.env

import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.api.PlannerSession
import zio.{URIO, ZIO}

/**
  * © Alexey Toroshchin 2020.
  */
trait PlannerSessionModifyRepo extends Serializable {
  val plannerSessionModifyRepo: PlannerSessionModifyRepo.Service[Any]
}

object PlannerSessionModifyRepo {
  trait Service[R] {
    def upsert(session: PlannerSession): URIO[R, List[PlannerSession]]
  }

  final object > extends Service[PlannerSessionModifyRepo] {
    def upsert(session: PlannerSession): URIO[PlannerSessionModifyRepo, List[PlannerSession]] =
      ZIO.accessM(_.plannerSessionModifyRepo.upsert(session))
  }
}
