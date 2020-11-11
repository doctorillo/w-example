package bookingtour.api.customers.endpoints.users

import bookingtour.api.customers.config.ApiRuntime
import bookingtour.protocols.core.values.api.QueryResult
import bookingtour.protocols.parties.api.ui.ContextEnvUI
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint

/**
  * © Alexey Toroshchin 2019.
  */
object EndpointUsers {
  final type PointType = QueryResult[ContextEnvUI]

  final def apply()(implicit ar: ApiRuntime, cs: ContextShift[IO]): Endpoint[IO, PointType] = {
    import ar.qcm._
    SignIn(signInModule).route
  }
}
