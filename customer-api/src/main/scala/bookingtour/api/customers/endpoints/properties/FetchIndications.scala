package bookingtour.api.customers.endpoints.properties

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.api.core.queries.FetchEnumQ
import bookingtour.protocols.core.values.api.EnumAPI
import bookingtour.protocols.core.values.enumeration.LangItem
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._

/**
  * © Alexey Toroshchin 2019.
  */
final class FetchIndications private (
    val repository: QueryModule[FetchEnumQ, EnumAPI]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[FetchEnumQ, EnumAPI] {
  val pointPath: String = "treatment-indications"

  override val endpoint: Endpoint[IO, FetchEnumQ] = get("api" :: pointPath :: path[Int]).map { langId: Int =>
    FetchEnumQ(LangItem.withValue(langId))
  }
}

object FetchIndications {
  final def apply(
      repository: QueryModule[FetchEnumQ, EnumAPI]
  )(implicit cs: ContextShift[IO]): ApiContractEndpoint[FetchEnumQ, EnumAPI] =
    new FetchIndications(repository)
}
