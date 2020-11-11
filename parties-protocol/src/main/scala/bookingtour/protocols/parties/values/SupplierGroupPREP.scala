package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.{ContextItem, SyncItem}
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.parties.newTypes.{ProviderId, SupplierGroupId}
import cats.instances.all._
import cats.syntax.option._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class SupplierGroupPREP(
    id: SupplierGroupId,
    providerId: ProviderId,
    code: Option[String],
    notes: Option[String],
    stamp: Instant
)

object SupplierGroupPREP {
  type Id = SupplierGroupId

  implicit final val itemR0: SupplierGroupPREP => Id = _.id

  implicit final val itemR1: SupplierGroupPREP => Instant = _.stamp

  implicit final val itemP0: SupplierGroupPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      providerId: UUID,
      code: Option[String],
      notes: Option[String],
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => SupplierGroupPREP = _.into[SupplierGroupPREP]
    .withFieldComputed(_.id, x => SupplierGroupId(x.id))
    .withFieldComputed(_.providerId, x => ProviderId(x.providerId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      providerId: UUID,
      code: Option[String],
      notes: Option[String],
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  final case class SupplierGroupInterLookSync(
      id: SupplierGroupId,
      partySync: LookPartyId,
      context: ContextItem
  )

  final val toSync: (
      LookPartyId,
      ContextItem
  ) => SyncItem.InterLook = (partySync, contextItem) =>
    SyncItem.InterLook(
      id = partySync,
      categoryId = contextItem.value.some
    )

  final val fromSync: SyncE => Option[SupplierGroupInterLookSync] =
    _.askInterLook.flatMap {
      case (dataId, syncId, categoryId) =>
        categoryId.map(ctx =>
          SupplierGroupInterLookSync(
            id = dataId.x,
            partySync = syncId,
            context = ContextItem.withValue(ctx)
          )
        )
    }
}
