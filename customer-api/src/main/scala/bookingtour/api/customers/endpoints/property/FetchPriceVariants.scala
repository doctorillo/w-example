package bookingtour.api.customers.endpoints.property

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.api.booking.FetchPriceVariantsQ
import bookingtour.protocols.property.prices.api.PriceUnitUI
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._
import io.finch.circe._

/**
  * © Alexey Toroshchin 2019.
  */
final class FetchPriceVariants private (
    val repository: QueryModule[FetchPriceVariantsQ, PriceUnitUI]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[FetchPriceVariantsQ, PriceUnitUI] {
  val pointPath: String = "price-variants"

  override val endpoint: Endpoint[IO, FetchPriceVariantsQ] =
    post("api" :: "property" :: pointPath :: jsonBody[FetchPriceVariantsQ])
}

object FetchPriceVariants {
  final def apply(
      repository: QueryModule[FetchPriceVariantsQ, PriceUnitUI]
  )(implicit cs: ContextShift[IO]): ApiContractEndpoint[FetchPriceVariantsQ, PriceUnitUI] =
    new FetchPriceVariants(repository)
}
