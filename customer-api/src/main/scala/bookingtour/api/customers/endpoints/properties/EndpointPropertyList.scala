package bookingtour.api.customers.endpoints.properties

import bookingtour.api.customers.config.ApiRuntime
import bookingtour.protocols.core.values.api.{EnumAPI, QueryResult}
import bookingtour.protocols.property.prices.api.PropertyCardUI
import cats.effect.{ContextShift, IO}
import io.finch.Endpoint
import shapeless.{:+:, CNil}

/**
  * © Alexey Toroshchin 2019.
  */
object EndpointPropertyList {
  final type PointType =
    QueryResult[PropertyCardUI] :+: QueryResult[EnumAPI] :+: QueryResult[
      EnumAPI
    ] :+: QueryResult[
      EnumAPI
    ] :+: QueryResult[EnumAPI] :+: QueryResult[EnumAPI] :+: CNil

  final def apply()(implicit ar: ApiRuntime, cs: ContextShift[IO]): Endpoint[IO, PointType] = {
    import ar.qcm._
    FetchCards(propertyCardsModule).route :+: FetchAmenities(amenityModule).route :+: FetchRoomFacilities(
      facilityModule
    ).route :+: FetchIndications(indicationsModule).route :+: FetchMedicalDepartment(medicalDepartmentModule).route :+: FetchTherapies(
      therapyModule
    ).route

  }
}
