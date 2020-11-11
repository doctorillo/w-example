package bookingtour.protocols.core.values.db

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, ImageId}
import bookingtour.protocols.core.newtypes.quantities.Position
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class ImageE(
    id: ImageId,
    dataId: DataId,
    path: String,
    position: Position,
    stamp: Instant
)

object ImageE {
  type Id = ImageId

  implicit final val itemR0: ImageE => Id = _.id

  implicit final val itemR1: ImageE => Instant = _.stamp

  implicit final val itemP0: ImageE => Int = _ => 0

  final case class Output(
      id: UUID,
      dataId: UUID,
      path: String,
      position: Int,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => ImageE = _.into[ImageE]
    .withFieldComputed(_.id, x => ImageId(x.id))
    .withFieldComputed(_.dataId, x => DataId(x.dataId))
    .withFieldComputed(_.position, x => Position(x.position))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(dataId: UUID, path: String, position: Int, solverId: Option[UUID] = None)

  implicit final val itemP1: Create => Int = _ => 0

  @derive(order)
  final case class Update(
      id: UUID,
      parent: Option[UUID],
      dataId: UUID,
      path: String,
      position: Int,
      deleted: Boolean,
      solverId: Option[UUID] = None
  )
}
