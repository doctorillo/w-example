package bookingtour.api.customers.endpoints.users

import java.util.UUID

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.core.finch.{ActiveEndpoints, ApiContractEndpoint}
import bookingtour.protocols.api.contexts.queries.AttachEnvQ
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.effect.{ContextShift, IO}
import io.finch._
import io.finch.catsEffect._
import shapeless.{::, HNil}

/**
  * © Alexey Toroshchin 2019.
  */
final class AttachEnv private (
    val repository: QueryModule[AttachEnvQ, UUID]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[AttachEnvQ, UUID] {
  val pointPath: String = "attach"

  override val endpoint: Endpoint[IO, AttachEnvQ] =
    get("api" :: pointPath :: path[Int] :: ActiveEndpoints.instances.contractEndpoint).map {
      case langId :: contract :: HNil =>
        AttachEnvQ(
          sessionId = contract.id,
          fingerprint = contract.fingerprint,
          ip = contract.ip,
          lang = LangItem.withValue(langId)
        )
    }
}

object AttachEnv {
  final def apply(repository: QueryModule[AttachEnvQ, UUID])(
      implicit cs: ContextShift[IO]
  ): ApiContractEndpoint[AttachEnvQ, UUID] =
    new AttachEnv(repository)
}
