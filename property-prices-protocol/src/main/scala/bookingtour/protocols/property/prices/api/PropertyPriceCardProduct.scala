package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.PropertyStar
import bookingtour.protocols.core.values.api.{DescriptionAPI, ImageAPI}
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.parties.newTypes._
import bookingtour.protocols.properties.agg.RoomUnitProduct
import bookingtour.protocols.properties.api.{BoardingProduct, PropertyCardProduct}
import bookingtour.protocols.properties.newTypes.{
  AmenityId,
  FacilityId,
  MedicalDepartmentId,
  PropertyId,
  TherapyId,
  TreatmentIndicationId
}
import bookingtour.protocols.property.prices.newTypes.PropertyProviderId
import cats.instances.all._
import bookingtour.protocols.core._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._
import tofu.logging.derivation.{loggable}

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order, loggable)
final case class PropertyPriceCardProduct(
    id: PropertyId,
    propertyProvider: PropertyProviderId,
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
    roomUnits: List[RoomUnitProduct],
    tariffs: List[TariffOp],
    variants: List[VariantOp],
    offers: List[OfferOp],
    offerRules: List[OfferRuleVILP],
    offerDates: List[OfferDateOp],
    priceUnits: List[PriceUnitOp]
)

object PropertyPriceCardProduct {
  type Id = PropertyProviderId

  implicit final val itemR0: PropertyPriceCardProduct => Id = _.propertyProvider

  implicit final val itemP0: PropertyPriceCardProduct => Int = _ => 0

  implicit final val itemP1: PropertyPriceCardProduct => CityId = _.city

  final val propertyPriceCardProduct: (
      PropertyProviderId,
      List[TariffOp],
      List[VariantOp],
      List[OfferOp],
      List[OfferRuleVILP],
      List[OfferDateOp],
      List[PriceUnitOp],
      PropertyCardProduct
  ) => PropertyPriceCardProduct =
    (propertyProvider, tariffs, variants, offers, offerRules, offerDates, priceUnits, card) =>
      card
        .into[PropertyPriceCardProduct]
        .withFieldComputed(_.propertyProvider, _ => propertyProvider)
        .withFieldComputed(_.variants, _ => variants)
        .withFieldComputed(_.tariffs, _ => tariffs)
        .withFieldComputed(_.offers, _ => offers)
        .withFieldComputed(_.offerRules, _ => offerRules)
        .withFieldComputed(_.offerDates, _ => offerDates)
        .withFieldComputed(_.priceUnits, _ => priceUnits)
        .transform
}
