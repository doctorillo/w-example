package bookingtour.protocols.parties.agg.basic

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.AddressItem
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.parties.newTypes.{AddressId, CityDistrictId, CityId, CountryId, PartyId, RegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class AddressAgg(
    id: AddressId,
    cityId: CityId,
    regionId: RegionId,
    countryId: CountryId,
    districtId: Option[CityDistrictId],
    partyId: PartyId,
    category: AddressItem,
    street: String,
    point: Option[GPoint]
)

object AddressAgg {
  type Id = AddressId

  implicit final val itemR0: AddressAgg => Id = _.id

  implicit final val itemP0: AddressAgg => Int = _ => 0
}
