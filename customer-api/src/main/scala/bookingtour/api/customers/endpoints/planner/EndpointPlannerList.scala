package bookingtour.api.customers.endpoints.planner

import bookingtour.api.customers.config.ApiRuntime
import bookingtour.protocols.core.values.api.QueryResult
import bookingtour.protocols.orders.api.PlannerSession
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import shapeless.{:+:, CNil}

/**
  * © Alexey Toroshchin 2020.
  */
object EndpointPlannerList {
  final type PointType =
    QueryResult[PlannerSession] :+: QueryResult[PlannerSession] :+: CNil

  final def apply()(implicit ar: ApiRuntime, cs: ContextShift[IO]): Endpoint[IO, PointType] = {
    import ar.qcm._
    import ar.qom._
    FetchPlanners(plannerSessionModule).route :+: UpsertPlanner(plannerModifyModule).route
  }
}
