package bookingtour.api.customers.endpoints.planner

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.api.PlannerSession
import bookingtour.protocols.orders.api.cmd.UpsertPlannerSessionQ
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._
import io.finch.circe._

/**
  * © Alexey Toroshchin 2020.
  */
final class UpsertPlanner private (
    val repository: QueryModule[UpsertPlannerSessionQ, PlannerSession]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[UpsertPlannerSessionQ, PlannerSession] {
  val pointPath: String = "planner"

  override val endpoint: Endpoint[IO, UpsertPlannerSessionQ] =
    post("api" :: pointPath :: jsonBody[PlannerSession]).map { session: PlannerSession =>
      UpsertPlannerSessionQ(session)
    }
}

object UpsertPlanner {
  final def apply(
      repository: QueryModule[UpsertPlannerSessionQ, PlannerSession]
  )(implicit cs: ContextShift[IO]): ApiContractEndpoint[UpsertPlannerSessionQ, PlannerSession] =
    new UpsertPlanner(repository = repository)
}
