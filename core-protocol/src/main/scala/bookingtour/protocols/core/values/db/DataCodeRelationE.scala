package bookingtour.protocols.core.values.db

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataCodeId, DataId}
import bookingtour.protocols.core.values.enumeration.SyncItem
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class DataCodeRelationE(
    id: DataCodeId,
    dataId: DataId,
    code: String,
    stamp: Instant
)

object DataCodeRelationE {
  type Id = DataCodeId

  implicit final val itemR0: DataCodeRelationE => Id = _.id

  implicit final val itemR1: DataCodeRelationE => Instant = _.stamp

  implicit final val itemP0: DataCodeRelationE => Int = _ => 0

  final case class Output(id: UUID, dataId: UUID, code: String, stamp: LocalDateTime)

  implicit final val outputTransform: Output => DataCodeRelationE =
    _.into[DataCodeRelationE]
      .withFieldComputed(_.id, x => DataCodeId(x.id))
      .withFieldComputed(_.dataId, x => DataId(x.dataId))
      .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
      .transform

  @derive(order)
  final case class Create(dataId: UUID, code: String, solverId: Option[UUID] = None)

  implicit final val itemP1: Create => Int = _ => 0

  @derive(order)
  final case class CreateWithSync(
      dataId: UUID,
      code: String,
      sync: SyncItem,
      solverId: Option[UUID] = None
  )

  implicit final val itemP2: CreateWithSync => Int = _ => 0
}
