package bookingtour.protocols.interlook.source.geo

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookCityId, LookCountryId, LookRegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CityEP(
    id: LookCityId,
    name: String,
    regionId: LookRegionId,
    regionName: String,
    countryId: LookCountryId,
    countryName: String
)

object CityEP {
  type Id = LookCityId

  implicit final val itemR: CityEP => Id = _.id

  implicit final val itemP: CityEP => Int = _ => 0

  implicit final val cityEP0: CityEP => CountryEP = _.into[CountryEP]
    .withFieldComputed(_.id, _.countryId)
    .withFieldComputed(_.name, _.countryName)
    .transform

  implicit final val cityEP1: CityEP => RegionEP = _.into[RegionEP]
    .withFieldComputed(_.id, _.regionId)
    .withFieldComputed(_.countryId, _.countryId)
    .withFieldComputed(_.name, _.regionName)
    .transform

  final case class Output(
      id: Int,
      name: String,
      regionId: Int,
      regionName: String,
      countryId: Int,
      countryName: String
  )

  implicit final val outputTransform: Output => CityEP = _.into[CityEP]
    .withFieldComputed(_.id, x => LookCityId(x.id))
    .withFieldComputed(_.countryId, x => LookCountryId(x.countryId))
    .withFieldComputed(_.regionId, x => LookRegionId(x.regionId))
    .transform
}
