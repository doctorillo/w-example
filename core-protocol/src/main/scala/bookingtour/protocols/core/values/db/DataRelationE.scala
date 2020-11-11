package bookingtour.protocols.core.values.db

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, DataValue, DataValueId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class DataRelationE(
    id: DataValueId,
    dataId: DataId,
    valueId: DataValue,
    stamp: Instant
)

object DataRelationE {
  type Id = DataValueId

  implicit final val itemR0: DataRelationE => Id = _.id

  implicit final val itemR1: DataRelationE => Instant = _.stamp

  implicit final val itemP0: DataRelationE => Int = _ => 0

  final case class Output(id: UUID, dataId: UUID, valueId: UUID, stamp: LocalDateTime)

  implicit final val outputTransform: Output => DataRelationE =
    _.into[DataRelationE]
      .withFieldComputed(_.id, x => DataValueId(x.id))
      .withFieldComputed(_.dataId, x => DataId(x.dataId))
      .withFieldComputed(_.valueId, x => DataValue(x.valueId))
      .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
      .transform

  @derive(order)
  final case class Create(dataId: UUID, valueId: UUID, solverId: Option[UUID] = None)

  implicit final val itemP1: Create => Int = _ => 0
}
