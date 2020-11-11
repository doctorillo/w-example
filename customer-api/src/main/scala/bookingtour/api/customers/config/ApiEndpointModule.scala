package bookingtour.api.customers.config

import bookingtour.api.customers.endpoints.ctx.EndpointsContext
import bookingtour.api.customers.endpoints.ctx.EndpointsContext
import bookingtour.api.customers.endpoints.excursions.EndpointExcursionList
import bookingtour.api.customers.endpoints.planner.EndpointPlannerList
import bookingtour.api.customers.endpoints.planner.EndpointPlannerList
import bookingtour.api.customers.endpoints.properties.EndpointPropertyList
import bookingtour.api.customers.endpoints.property.EndpointProperty
import bookingtour.api.customers.endpoints.property.EndpointProperty
import bookingtour.api.customers.endpoints.users.EndpointUsers
import bookingtour.api.customers.endpoints.users.EndpointUsers
import bookingtour.core.finch.ContractFilter
import bookingtour.protocols.core.sessions.{FingerprintValue, RemoteValue, SessionIdValue}
import bookingtour.protocols.parties.api.sessions.EnvApi
import cats.effect.{ContextShift, IO}
import com.twitter.finagle.Filter
import com.twitter.finagle.http.filter.CorsFilter
import com.twitter.finagle.http.{HttpMuxer, Request, Response}
import com.typesafe.scalalogging.Logger
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import zio.Task

/**
  * © Alexey Toroshchin 2019.
  */
final class ApiEndpointModule(val endpoints: HttpMuxer)

object ApiEndpointModule {
  final val productionPolicy: Filter[Request, Response, Request, Response] = CorsFilter(
    origin = "http://localhost:3000",
    methods = "GET,OPTION,POST,HEAD,DELETE,PUT",
    headers =
      "Access-Control-Allow-Origin, Access-Control-Allow-Headers, Access-Control-Allow-Methods, access-control-allow-credentials, Accept, Content-Type, x-requested-with, x-session-id, x-request-id",
    exposes =
      "x-session-id, x-request-id, Access-Control-Allow-Origin, Access-Control-Allow-Headers, Access-Control-Allow-Methods, Access-Control-Allow-Credentials, Content-Type"
  )
  final def sessionFilter(
      assign: (SessionIdValue, FingerprintValue, RemoteValue) => Task[EnvApi]
  )(
      implicit zioRuntime: zio.Runtime[zio.ZEnv],
      log: Logger
  ): ContractFilter = ContractFilter(assign)

  final def apply()(implicit ar: ApiRuntime, cs: ContextShift[IO]): ApiEndpointModule = {
    import ar.bm._
    import ar.rm._
    val health = get("health") {
      Ok("")
    }
    val services =
      (
        EndpointsContext() :+: EndpointPropertyList() :+: EndpointProperty() :+: EndpointUsers() :+: EndpointPlannerList() :+: EndpointExcursionList()
      ).handle {
        case thr: Throwable =>
          ar.bm.log.error(s"handle service: ", thr)
          Output.failure(new Exception(thr))
      }.toServiceAs[Application.Json]
    val mx = HttpMuxer
      .withHandler(
        "api/",
        productionPolicy
          .andThen(sessionFilter(ar.sm.assignSession))
          .andThen(services)
      )
      .withHandler("health", health.toServiceAs[Text.Plain])
    new ApiEndpointModule(mx)
  }
}
