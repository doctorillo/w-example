package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{CityDistrictId, CityId, CountryId, RegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CityDistrictPREP(
    id: CityDistrictId,
    cityId: CityId,
    regionId: RegionId,
    countryId: CountryId,
    name: String,
    stamp: Instant
)

object CityDistrictPREP {
  type Id = CityDistrictId

  implicit final val itemR0: CityDistrictPREP => Id = _.id

  implicit final val itemR1: CityDistrictPREP => Instant = _.stamp

  implicit final val itemP0: CityDistrictPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      cityId: UUID,
      regionId: UUID,
      countryId: UUID,
      name: String,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => CityDistrictPREP = _.into[CityDistrictPREP]
    .withFieldComputed(_.id, x => CityDistrictId(x.id))
    .withFieldComputed(_.cityId, x => CityId(x.cityId))
    .withFieldComputed(_.regionId, x => RegionId(x.regionId))
    .withFieldComputed(_.countryId, x => CountryId(x.countryId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(cityId: UUID, name: String)

  implicit final val itemP1: Create => Int = _ => 0
}
