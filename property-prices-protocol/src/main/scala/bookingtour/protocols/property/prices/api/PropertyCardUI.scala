package bookingtour.protocols.property.prices.api

import bookingtour.protocols.core.newtypes.quantities.{Counter, PropertyStar}
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.parties.newTypes.{CityDistrictId, CityId, CountryId, PartyId, RegionId}
import bookingtour.protocols.properties.api.{BoardingUI, PropertyImageProduct}
import bookingtour.protocols.properties.newTypes.{
  AmenityId,
  FacilityId,
  MedicalDepartmentId,
  PropertyId,
  TherapyId,
  TreatmentIndicationId
}
import cats.syntax.order._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import cats.instances.all._
import bookingtour.protocols.core._
import bookingtour.protocols.core.values.api.ImageAPI

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyCardUI(
    id: PropertyId,
    lang: LangItem,
    partyId: PartyId,
    supplierId: PartyId,
    countryId: CountryId,
    regionId: RegionId,
    cityId: CityId,
    districtId: Option[CityDistrictId],
    name: String,
    star: PropertyStar,
    address: String,
    location: Option[GPoint],
    images: List[ImageAPI],
    boardings: List[BoardingUI],
    amenities: List[AmenityId],
    facilities: List[FacilityId],
    indications: List[TreatmentIndicationId],
    medicals: List[MedicalDepartmentId],
    therapies: List[TherapyId],
    bestPrice: BestPriceUI
)

object PropertyCardUI {
  type Id = PropertyId

  implicit final val itemR: PropertyCardUI => Id = _.id

  implicit final val itemP: PropertyCardUI => CityId = _.cityId

  final val byPriceOrdering: Ordering[PropertyCardUI] = (x: PropertyCardUI, y: PropertyCardUI) =>
    x.bestPrice.total.compare(y.bestPrice.total)

  def fromSource(
      lang: LangItem,
      card: PropertyPriceCardProduct,
      prices: List[PriceVariantUI]
  ): PropertyCardUI = {
    val a = prices.sortWith((l, r) => l.total > r.total).head
    val b = card.boardings
      .filter(x => prices.exists(_.boardingId === x.id))
      .flatMap(x =>
        x.names
          .find(_.lang === lang)
          .toList
          .map(z =>
            BoardingUI(
              id = x.category,
              code = x.categoryCode,
              withTreatment = x.treatment,
              label = z
            )
          )
      )
    PropertyCardUI(
      id = card.id,
      lang = lang,
      partyId = card.property,
      supplierId = card.supplier,
      countryId = card.country,
      regionId = card.region,
      cityId = card.city,
      districtId = card.district,
      name = card.name,
      star = card.star,
      address = card.address,
      location = card.location,
      images = card.images,
      boardings = b,
      amenities = card.amenities,
      facilities = card.facilities,
      indications = card.indications,
      medicals = card.medicals,
      therapies = card.therapies,
      bestPrice = BestPriceUI(
        priceUnitId = a.priceUnitId,
        lang = lang,
        roomType = a.roomType,
        roomCategory = a.roomCategory,
        boarding = a.boarding,
        nights = a.nights,
        tariff = a.tariff,
        pax = a.pax,
        stopSale = a.stopSale,
        resultCount = Counter(prices.length),
        price = a.amount,
        discount = a.discount,
        total = a.total
      )
    )
  }
}
