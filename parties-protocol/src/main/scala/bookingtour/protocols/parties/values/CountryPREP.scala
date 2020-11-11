package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.interlook.source.geo.CountryEP
import bookingtour.protocols.interlook.source.newTypes.LookCountryId
import bookingtour.protocols.parties.newTypes.CountryId
import bookingtour.protocols.parties.newTypes.CountryId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class CountryPREP(
    id: CountryId,
    isoCode2: Option[String],
    isoCode3: Option[String],
    name: String,
    stamp: Instant
)

object CountryPREP {
  type Id = CountryId

  implicit final val itemR0: CountryPREP => Id = _.id

  implicit final val itemR1: CountryPREP => Instant = _.stamp

  implicit final val itemP0: CountryPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      isoCode2: Option[String],
      isoCode3: Option[String],
      name: String,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => CountryPREP = _.into[CountryPREP]
    .withFieldComputed(_.id, x => CountryId(x.id))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(name: String, sync: SyncItem, solverId: Option[UUID] = None)

  implicit final val toCreateT: CountryEP => CountryPREP.Create =
    _.into[CountryPREP.Create]
      .withFieldComputed(_.sync, x => SyncItem.InterLook(id = x.id.x))
      .withFieldConst(_.solverId, None)
      .transform

  implicit final val itemP1: Create => Int = _ => 0

  final case class CountryInterLookSync(id: CountryId, country: LookCountryId)

  final val toSync: LookCountryId => SyncItem.InterLook = x => SyncItem.InterLook(id = x.x)

  final val fromSync: SyncE => Option[CountryInterLookSync] =
    _.askInterLook.map(x => CountryInterLookSync(id = CountryId(x._1.x), country = LookCountryId(x._2)))
}
