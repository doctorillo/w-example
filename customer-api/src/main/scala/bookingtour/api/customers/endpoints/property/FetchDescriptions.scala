package bookingtour.api.customers.endpoints.property

import java.util.UUID

import bookingtour.core.actors.kafka.queries.QueryModule
import bookingtour.core.finch.ApiContractEndpoint
import bookingtour.protocols.api.booking.FetchPropertyDescriptionsQ
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.properties.api.PropertyDescriptionUI
import bookingtour.protocols.properties.newTypes.PropertyId
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import io.finch.catsEffect._
import shapeless.{::, HNil}

/**
  * © Alexey Toroshchin 2019.
  */
final class FetchDescriptions private (
    val repository: QueryModule[FetchPropertyDescriptionsQ, PropertyDescriptionUI]
)(implicit val cs: ContextShift[IO])
    extends ApiContractEndpoint[FetchPropertyDescriptionsQ, PropertyDescriptionUI] {
  val pointPath: String = "descriptions"

  override val endpoint: Endpoint[IO, FetchPropertyDescriptionsQ] =
    get("api" :: "property" :: pointPath :: path[UUID] :: path[Int]).map {
      case propertyId :: lang :: HNil =>
        FetchPropertyDescriptionsQ(
          id = PropertyId(propertyId),
          lang = LangItem.withValue(lang)
        )
    }
}

object FetchDescriptions {
  final def apply(repository: QueryModule[FetchPropertyDescriptionsQ, PropertyDescriptionUI])(
      implicit cs: ContextShift[IO]
  ): ApiContractEndpoint[FetchPropertyDescriptionsQ, PropertyDescriptionUI] =
    new FetchDescriptions(repository)
}
