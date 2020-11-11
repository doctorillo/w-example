package bookingtour.protocols.core.values.db

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{DataId, SyncId}
import bookingtour.protocols.core.values.enumeration.SyncItem.Meta
import bookingtour.protocols.core.values.enumeration.{SyncItem, SyncSourceItem}
import cats.data.NonEmptyList
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class SyncE(
    id: SyncId,
    dataId: DataId,
    sync: SyncItem,
    active: Boolean,
    stamp: Instant
)

object SyncE {
  type Id = SyncId

  implicit final val itemR0: SyncE => Id = _.id

  implicit final val itemR1: SyncE => Instant = _.stamp

  implicit final val itemP: SyncE => Int = _ => 0

  implicit final val maxStamp: (List[SyncE], Instant) => Instant = (xs, default) =>
    NonEmptyList.fromList(xs).map(xs => xs.map(_.stamp).toList.max).getOrElse(default)

  final case class Output(
      id: UUID,
      dataId: UUID,
      source: SyncSourceItem,
      sync: SyncItem,
      active: Boolean,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => SyncE =
    _.into[SyncE]
      .withFieldComputed(_.id, x => SyncId(x.id))
      .withFieldComputed(_.dataId, x => DataId(x.dataId))
      .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
      .transform

  @derive(order)
  final case class Create(dataId: UUID, sync: SyncItem, solverId: Option[UUID] = None)

  @derive(order)
  final case class Update(
      id: UUID,
      parent: Option[UUID],
      solverId: Option[UUID],
      dataId: UUID,
      sync: SyncItem,
      active: Boolean,
      deleted: Boolean
  )

  implicit final class SyncEOps(private val self: SyncE) extends AnyVal {
    def askInterLook: Option[(DataId, Int, Option[Int])] =
      self.sync.toInterLook.map(sync => (self.dataId, sync.id, sync.categoryId))

    def askInterLookRich: Option[(DataId, Meta)] =
      self.sync.toInterLookRich.map(sync => (self.dataId, sync.meta))
  }
}
