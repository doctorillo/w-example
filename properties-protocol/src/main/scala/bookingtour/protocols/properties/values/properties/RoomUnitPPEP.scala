package bookingtour.protocols.properties.values.properties

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.properties.newTypes.{RoomCategoryId, RoomTypeId, RoomUnitId}
import derevo.cats.order
import derevo.derive
import io.scalaland.chimney.dsl._
import cats.instances.all._
import bookingtour.protocols.core._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order)
final case class RoomUnitPPEP(
    id: RoomUnitId,
    typeId: RoomTypeId,
    categoryId: RoomCategoryId,
    stamp: Instant
)

object RoomUnitPPEP {
  type Id = RoomUnitId

  implicit final val itemR0: RoomUnitPPEP => Id = _.id

  implicit final val itemR1: RoomUnitPPEP => Instant = _.stamp

  implicit final val itemP0: RoomUnitPPEP => Int = _ => 0

  final case class Output(
      id: UUID,
      typeId: UUID,
      categoryId: UUID,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => RoomUnitPPEP = _.into[RoomUnitPPEP]
    .withFieldComputed(_.id, x => RoomUnitId(x.id))
    .withFieldComputed(_.typeId, x => RoomTypeId(x.typeId))
    .withFieldComputed(_.categoryId, x => RoomCategoryId(x.categoryId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      typeId: UUID,
      typeCode: String,
      categoryId: UUID,
      categoryCode: String,
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0
}
