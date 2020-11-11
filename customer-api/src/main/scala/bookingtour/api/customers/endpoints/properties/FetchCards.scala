package bookingtour.api.customers.endpoints.properties

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.api.booking.FetchPropertyCardQ
import bookingtour.protocols.property.prices.api.PropertyCardUI
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._
import io.finch.circe._

/**
  * © Alexey Toroshchin 2019.
  */
final class FetchCards private (
    val repository: QueryModule[FetchPropertyCardQ, PropertyCardUI]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[FetchPropertyCardQ, PropertyCardUI] {
  val pointPath: String = "search"

  override val endpoint: Endpoint[IO, FetchPropertyCardQ] =
    post("api" :: "properties" :: pointPath :: jsonBody[FetchPropertyCardQ])
}

object FetchCards {
  final def apply(
      repository: QueryModule[FetchPropertyCardQ, PropertyCardUI]
  )(
      implicit cs: ContextShift[IO]
  ): ApiContractEndpoint[FetchPropertyCardQ, PropertyCardUI] =
    new FetchCards(repository)
}
