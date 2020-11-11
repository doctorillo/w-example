package bookingtour.protocols.orders.api

import bookingtour.protocols.core.newtypes.quantities.PropertyStar
import bookingtour.protocols.properties.newTypes.{
  AmenityId,
  CategoryBoardingId,
  FacilityId,
  MedicalDepartmentId,
  TherapyId,
  TreatmentIndicationId
}
import bookingtour.protocols.core._
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PlannerPropertyFilterParams(
    name: Option[String],
    stars: List[PropertyStar],
    price: List[Double],
    boardings: List[CategoryBoardingId],
    amenities: List[AmenityId],
    facilities: List[FacilityId],
    medicals: List[MedicalDepartmentId],
    indications: List[TreatmentIndicationId],
    therapies: List[TherapyId],
    viewStop: Boolean
)
