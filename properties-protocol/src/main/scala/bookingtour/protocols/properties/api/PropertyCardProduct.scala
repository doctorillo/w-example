package bookingtour.protocols.properties.api

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.PropertyStar
import bookingtour.protocols.core.values.api.{DescriptionAPI, ImageAPI}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.newTypes._
import bookingtour.protocols.parties.values.PartyPREP
import bookingtour.protocols.properties.agg.RoomUnitProduct
import bookingtour.protocols.properties.newTypes.{
  AmenityId,
  FacilityId,
  MedicalDepartmentId,
  PropertyId,
  TherapyId,
  TreatmentIndicationId
}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyCardProduct(
    id: PropertyId,
    property: PartyId,
    propertySync: List[SyncItem],
    supplier: PartyId,
    supplierName: String,
    supplierSync: List[SyncItem],
    country: CountryId,
    region: RegionId,
    city: CityId,
    district: Option[CityDistrictId],
    name: String,
    star: PropertyStar,
    address: String,
    location: Option[GPoint],
    description: List[DescriptionAPI],
    guestTerms: List[DescriptionAPI],
    paymentTerms: List[DescriptionAPI],
    cancellationTerms: List[DescriptionAPI],
    taxTerms: List[DescriptionAPI],
    images: List[ImageAPI],
    boardings: List[BoardingProduct],
    amenities: List[AmenityId],
    facilities: List[FacilityId],
    indications: List[TreatmentIndicationId],
    medicals: List[MedicalDepartmentId],
    therapies: List[TherapyId],
    roomUnits: List[RoomUnitProduct]
)

object PropertyCardProduct {
  type Id = PropertyId

  implicit final val itemR0: PropertyCardProduct => Id = _.id

  implicit final val itemP0: PropertyCardProduct => Int = _ => 0

  implicit final val itemP1: PropertyCardProduct => CityId = _.city

  implicit final class PropertyCardProductOps(private val self: PropertyCardProduct) {
    def lookPropertySync: Option[LookPartyId] =
      self.propertySync.flatMap(PartyPREP.fromSyncItem(_).toList).headOption
    def lookSupplierSync: Option[LookPartyId] =
      self.supplierSync.flatMap(PartyPREP.fromSyncItem(_).toList).headOption
  }
}
