package bookingtour.api.customers.endpoints.excursions

import bookingtour.api.customers.config.ApiRuntime
import bookingtour.protocols.core.values.api.{EnumAPI, QueryResult}
import bookingtour.protocols.excursions.api.ExcursionCardUI
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import shapeless.{:+:, CNil}

/**
  * © Alexey Toroshchin 2020.
  */
object EndpointExcursionList {
  final type PointType = QueryResult[EnumAPI] :+: QueryResult[ExcursionCardUI] :+: CNil

  final def apply()(implicit ar: ApiRuntime, cs: ContextShift[IO]): Endpoint[IO, PointType] = {
    import ar.qcm._
    FetchTags(excursionTagsModule).route :+: FetchCards(excursionCardsModule).route
  }
}
