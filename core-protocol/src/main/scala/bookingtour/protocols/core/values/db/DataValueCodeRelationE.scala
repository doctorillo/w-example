package bookingtour.protocols.core.values.db

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, DataValue, DataValueCodeId}
import bookingtour.protocols.core.values.enumeration.SyncItem
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class DataValueCodeRelationE(
    id: DataValueCodeId,
    dataId: DataId,
    valueId: DataValue,
    code: String,
    stamp: Instant
)

object DataValueCodeRelationE {
  type Id = DataValueCodeId

  implicit final val itemR0: DataValueCodeRelationE => Id = _.id

  implicit final val itemR1: DataValueCodeRelationE => Instant = _.stamp

  implicit final val itemP0: DataValueCodeRelationE => Int = _ => 0

  final case class Output(id: UUID, dataId: UUID, valueId: UUID, code: String, stamp: LocalDateTime)

  implicit final val outputTransform: Output => DataValueCodeRelationE =
    _.into[DataValueCodeRelationE]
      .withFieldComputed(_.id, x => DataValueCodeId(x.id))
      .withFieldComputed(_.dataId, x => DataId(x.dataId))
      .withFieldComputed(_.valueId, x => DataValue(x.valueId))
      .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
      .transform

  @derive(order)
  final case class Create(dataId: UUID, valueId: UUID, code: String, solverId: Option[UUID] = None)

  implicit final val itemP1: Create => Int = _ => 0

  @derive(order)
  final case class CreateWithSync(
      dataId: UUID,
      valueId: UUID,
      code: String,
      sync: SyncItem,
      solverId: Option[UUID] = None
  )

  implicit final val itemP2: CreateWithSync => Int = _ => 0
}
