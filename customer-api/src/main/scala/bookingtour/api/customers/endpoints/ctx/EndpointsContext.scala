package bookingtour.api.customers.endpoints.ctx

import bookingtour.api.customers.config.ApiRuntime
import bookingtour.protocols.core.values.api.QueryResult
import bookingtour.protocols.parties.api.{PartyValue, PointUI}
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import shapeless.{:+:, CNil}

/**
  * © Alexey Toroshchin 2019.
  */
object EndpointsContext {
  final type PointType = QueryResult[PointUI] :+: QueryResult[PartyValue] :+: CNil

  final def apply()(implicit ar: ApiRuntime, cs: ContextShift[IO]): Endpoint[IO, PointType] = {
    import ar.qcm._
    FetchPoint(pointsPropertyModule).route :+: FetchBusinessRelations(businessRelationModule).route
  }
}
