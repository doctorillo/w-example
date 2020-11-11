package bookingtour.api.customers.endpoints.property

import bookingtour.api.customers.config.ApiRuntime
import bookingtour.protocols.core.values.api.QueryResult
import bookingtour.protocols.properties.api.PropertyDescriptionUI
import bookingtour.protocols.property.prices.api.PriceUnitUI
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import shapeless.{:+:, CNil}

/**
  * © Alexey Toroshchin 2019.
  */
object EndpointProperty {
  final type PointType = QueryResult[PriceUnitUI] :+: QueryResult[PropertyDescriptionUI] :+: CNil

  final def apply()(implicit ar: ApiRuntime, cs: ContextShift[IO]): Endpoint[IO, PointType] = {
    import ar.qcm._
    FetchPriceVariants(priceUnitModule).route :+: FetchDescriptions(propertyDescriptionsModule).route
  }
}
