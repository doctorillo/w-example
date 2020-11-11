package bookingtour.protocols.orders.env.live

import bookingtour.core.actors.kafka.state.ConsumerAlg
import bookingtour.core.actors.kafka.state.ConsumerAlg.Aux
import bookingtour.protocols.core._
import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.env.PlannerSessionRepo
import bookingtour.protocols.parties.newTypes.SolverId
import cats.instances.all._
import cats.syntax.order._
import zio.ZIO

/**
  * © Alexey Toroshchin 2020.
  */
final class LivePlannerSessionRepo private (service: ConsumerAlg.Aux[Any, SolverId, PlannerSession])
    extends PlannerSessionRepo {
  val plannerSessionRepo: PlannerSessionRepo.Service[Any] = (solverId: SolverId) =>
    service.byKey(_ === solverId).catchAll(_ => ZIO.succeed(List.empty))
}

object LivePlannerSessionRepo {
  def apply(service: Aux[Any, SolverId, PlannerSession]): PlannerSessionRepo =
    new LivePlannerSessionRepo(service)
}
