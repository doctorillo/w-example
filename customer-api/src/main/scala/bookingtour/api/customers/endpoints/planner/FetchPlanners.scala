package bookingtour.api.customers.endpoints.planner

import java.util.UUID

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.api.cmd.FetchPlannerSessionQ
import bookingtour.protocols.parties.newTypes.SolverId
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._

/**
  * © Alexey Toroshchin 2020.
  */
final class FetchPlanners private (
    val repository: QueryModule[FetchPlannerSessionQ, PlannerSession]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[FetchPlannerSessionQ, PlannerSession] {
  val pointPath: String = "planners"

  override val endpoint: Endpoint[IO, FetchPlannerSessionQ] =
    get("api" :: pointPath :: path[UUID]).map { solverId: UUID => FetchPlannerSessionQ(SolverId(solverId)) }
}

object FetchPlanners {
  final def apply(
      repository: QueryModule[FetchPlannerSessionQ, PlannerSession]
  )(implicit cs: ContextShift[IO]): ApiContractEndpoint[FetchPlannerSessionQ, PlannerSession] =
    new FetchPlanners(repository = repository)
}
