package bookingtour.api.customers.endpoints.excursions

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.excursions.FetchExcursionCardQ
import bookingtour.protocols.excursions.api.ExcursionCardUI
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._
import io.finch.circe._

/**
  * © Alexey Toroshchin 2020.
  */
final class FetchCards private (
    val repository: QueryModule[FetchExcursionCardQ, ExcursionCardUI]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[FetchExcursionCardQ, ExcursionCardUI] {
  val pointPath: String = "search"

  override val endpoint: Endpoint[IO, FetchExcursionCardQ] =
    post("api" :: "excursions" :: pointPath :: jsonBody[FetchExcursionCardQ])
}

object FetchCards {
  final def apply(
      repository: QueryModule[FetchExcursionCardQ, ExcursionCardUI]
  )(
      implicit cs: ContextShift[IO]
  ): ApiContractEndpoint[FetchExcursionCardQ, ExcursionCardUI] =
    new FetchCards(repository)
}
