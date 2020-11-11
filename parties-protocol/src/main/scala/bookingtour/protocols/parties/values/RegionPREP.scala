package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.interlook.source.newTypes.LookRegionId
import bookingtour.protocols.parties.newTypes.{CountryId, RegionId}
import bookingtour.protocols.parties.newTypes.{CountryId, RegionId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class RegionPREP(
    id: RegionId,
    countryId: CountryId,
    isoCode: Option[String],
    name: String,
    stamp: Instant
)

object RegionPREP {
  type Id = RegionId

  implicit final val itemR0: RegionPREP => Id = _.id

  implicit final val itemR1: RegionPREP => Instant = _.stamp

  implicit final val itemP0: RegionPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      countryId: UUID,
      isoCode: Option[String],
      name: String,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => RegionPREP = _.into[RegionPREP]
    .withFieldComputed(_.id, x => RegionId(x.id))
    .withFieldComputed(_.countryId, x => CountryId(x.countryId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      countryId: UUID,
      name: String,
      sync: SyncItem,
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  final case class RegionInterLookSync(id: RegionId, region: LookRegionId)

  final val toSync: LookRegionId => SyncItem.InterLook = x => SyncItem.InterLook(id = x.x)

  final val fromSync: SyncE => Option[RegionInterLookSync] =
    _.askInterLook.map(x => RegionInterLookSync(id = RegionId(x._1.x), region = LookRegionId(x._2)))
}
