package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{PointItem, SyncItem}
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.parties.newTypes.{CityId, CountryId, PickupPointId, RegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(order)
final case class PickupPointPREP(
    id: PickupPointId,
    cityId: CityId,
    regionId: RegionId,
    countryId: CountryId,
    name: String,
    pointType: PointItem,
    location: GPoint,
    stamp: Instant
)

object PickupPointPREP {
  type Id = PickupPointId

  implicit final val itemR0: PickupPointPREP => Id = _.id

  implicit final val itemR1: PickupPointPREP => Instant = _.stamp

  implicit final val itemP0: PickupPointPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      cityId: UUID,
      regionId: UUID,
      countryId: UUID,
      name: String,
      pointType: Int,
      location: GPoint,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => PickupPointPREP = _.into[PickupPointPREP]
    .withFieldComputed(_.id, x => PickupPointId(x.id))
    .withFieldComputed(_.cityId, x => CityId(x.cityId))
    .withFieldComputed(_.regionId, x => RegionId(x.regionId))
    .withFieldComputed(_.countryId, x => CountryId(x.countryId))
    .withFieldComputed(_.pointType, x => PointItem.withValue(x.pointType))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      cityId: CityId,
      name: String,
      pointType: PointItem,
      location: GPoint,
      sync: SyncItem,
      solverId: Option[UUID] = None
  )
  implicit final val itemP1: Create => Int = _ => 0

  /*@derive(order)
  final case class Upsert(
    id: PickupPointId,
    parent: Option[PickupPointId],
    solverId: Option[SolverId],
    cityId: CityId,
    name: String,
    pointType: PointItem,
    location: GPoint,
    deleted: Boolean
  )*/
}
