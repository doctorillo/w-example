package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.AddressItem
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.parties.agg.basic.AddressAgg
import bookingtour.protocols.parties.newTypes.{AddressId, CityDistrictId, CityId, CountryId, PartyId, RegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AddressPREP(
    id: AddressId,
    cityId: CityId,
    regionId: RegionId,
    countryId: CountryId,
    districtId: Option[CityDistrictId],
    partyId: PartyId,
    category: AddressItem,
    street: String,
    internal: Option[String],
    location: Option[String],
    zip: Option[String],
    point: Option[GPoint],
    stamp: Instant
)

object AddressPREP {
  type Id = AddressId

  implicit final val itemR0: AddressPREP => Id = _.id

  implicit final val itemR1: AddressPREP => Instant = _.stamp

  implicit final val itemP0: AddressPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      cityId: UUID,
      regionId: UUID,
      countryId: UUID,
      districtId: Option[UUID],
      partyId: UUID,
      category: Int,
      street: String,
      internal: Option[String],
      location: Option[String],
      zip: Option[String],
      point: Option[GPoint],
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => AddressPREP = _.into[AddressPREP]
    .withFieldComputed(_.id, x => AddressId(x.id))
    .withFieldComputed(_.cityId, x => CityId(x.cityId))
    .withFieldComputed(_.regionId, x => RegionId(x.regionId))
    .withFieldComputed(_.countryId, x => CountryId(x.countryId))
    .withFieldComputed(_.districtId, _.districtId.map(CityDistrictId(_)))
    .withFieldComputed(_.partyId, x => PartyId(x.partyId))
    .withFieldComputed(_.category, x => AddressItem.withValue(x.category))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      cityId: UUID,
      partyId: UUID,
      category: AddressItem,
      street: String,
      zip: Option[String],
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  implicit final val toAgg: AddressPREP => AddressAgg = _.into[AddressAgg].transform
}
