package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.doobie.values.parties.GPoint
import bookingtour.protocols.interlook.source.newTypes.LookCityId
import bookingtour.protocols.parties.newTypes.{CityId, CountryId, RegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CityPREP(
    id: CityId,
    regionId: RegionId,
    countryId: CountryId,
    isoCode: Option[String],
    name: String,
    point: Option[GPoint],
    hasDistricts: Boolean,
    stamp: Instant
)

object CityPREP {
  type Id = CityId

  implicit final val itemR0: CityPREP => Id = _.id

  implicit final val itemR1: CityPREP => Instant = _.stamp

  implicit final val itemP0: CityPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      regionId: UUID,
      countryId: UUID,
      isoCode: Option[String],
      name: String,
      point: Option[GPoint],
      hasDistricts: Boolean,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => CityPREP = _.into[CityPREP]
    .withFieldComputed(_.id, x => CityId(x.id))
    .withFieldComputed(_.regionId, x => RegionId(x.regionId))
    .withFieldComputed(_.countryId, x => CountryId(x.countryId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      name: String,
      regionId: UUID,
      sync: SyncItem,
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  final case class CityInterLookSync(id: CityId, city: LookCityId)

  final val toSync: LookCityId => SyncItem.InterLook = x => SyncItem.InterLook(id = x)

  final val fromSync: SyncE => Option[CityInterLookSync] =
    _.askInterLook.map(x => CityInterLookSync(id = x._1.x, city = x._2))
}
