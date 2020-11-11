package bookingtour.api.customers.endpoints.users

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.api.users.SignInQ
import bookingtour.protocols.parties.api.ui.ContextEnvUI
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._
import io.finch.circe._

/**
  * © Alexey Toroshchin 2019.
  */
final class SignIn private (
    val repository: QueryModule[SignInQ, ContextEnvUI]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[SignInQ, ContextEnvUI] {
  val pointPath: String = "sign-in"

  override val endpoint: Endpoint[IO, SignInQ] =
    post("api" :: pointPath :: jsonBody[SignInQ])
}

object SignIn {
  final def apply(repository: QueryModule[SignInQ, ContextEnvUI])(
      implicit cs: ContextShift[IO]
  ): ApiContractEndpoint[SignInQ, ContextEnvUI] =
    new SignIn(repository)
}
