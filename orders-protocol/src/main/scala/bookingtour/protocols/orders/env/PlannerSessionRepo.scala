package bookingtour.protocols.orders.env

import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.parties.newTypes.SolverId
import zio.{URIO, ZIO}

/**
  * © Alexey Toroshchin 2020.
  */
trait PlannerSessionRepo extends Serializable {
  val plannerSessionRepo: PlannerSessionRepo.Service[Any]
}

object PlannerSessionRepo {
  trait Service[R] {
    def fetch(solver: SolverId): URIO[R, List[PlannerSession]]
  }

  final object > extends Service[PlannerSessionRepo] {
    def fetch(solver: SolverId): URIO[PlannerSessionRepo, List[PlannerSession]] =
      ZIO.accessM(_.plannerSessionRepo.fetch(solver))
  }
}
