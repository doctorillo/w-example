package bookingtour.protocols.interlook.source.geo

import java.time.{Instant, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core._
import bookingtour.protocols.interlook.source.newTypes.{LookCityId, LookPickupPointId, LookPickupPointType}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class PickupPointEP(
    id: LookPickupPointId,
    cityId: LookCityId,
    typeId: LookPickupPointType,
    name: String,
    stamp: Instant
)

object PickupPointEP {
  type Id = LookPickupPointId

  implicit final val itemR: PickupPointEP => Id = _.id

  implicit final val itemP: PickupPointEP => Int = _ => 0

  final case class Output(id: Int, cityId: Int, typeId: Int, name: String, stamp: LocalDateTime)

  implicit final val outputTransform: Output => PickupPointEP = _.into[PickupPointEP]
    .withFieldComputed(_.id, x => LookPickupPointId(x.id))
    .withFieldComputed(_.cityId, x => LookCityId(x.cityId))
    .withFieldComputed(_.typeId, x => LookPickupPointType(x.typeId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform
}
