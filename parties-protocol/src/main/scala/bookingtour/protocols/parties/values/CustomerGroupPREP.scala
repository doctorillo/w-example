package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem.Meta
import bookingtour.protocols.core.values.enumeration.{ContextItem, SyncItem}
import bookingtour.protocols.interlook.source.newTypes.{LookCustomerGroupId, LookPartyId}
import bookingtour.protocols.parties.newTypes.{CustomerGroupId, ProviderId}
import cats.Applicative
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
final case class CustomerGroupPREP(
    id: CustomerGroupId,
    providerId: ProviderId,
    code: Option[String],
    notes: Option[String],
    stamp: Instant
)

object CustomerGroupPREP {
  type Id = CustomerGroupId

  implicit final val itemR0: CustomerGroupPREP => Id = _.id

  implicit final val itemR1: CustomerGroupPREP => Instant = _.stamp

  implicit final val itemP0: CustomerGroupPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      providerId: UUID,
      code: Option[String],
      notes: Option[String],
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => CustomerGroupPREP = _.into[CustomerGroupPREP]
    .withFieldComputed(_.id, x => CustomerGroupId(x.id))
    .withFieldComputed(_.providerId, x => ProviderId(x.providerId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      providerId: UUID,
      code: Option[String],
      notes: Option[String],
      sync: Option[SyncItem],
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  final case class CustomerGroupInterLookSync(
      id: CustomerGroupId,
      provider: ProviderId,
      sync: Option[LookCustomerGroupId],
      partySync: LookPartyId,
      context: ContextItem
  )

  final val toSync: (
      Option[LookCustomerGroupId],
      LookPartyId,
      ProviderId,
      ContextItem
  ) => SyncItem.InterLookRich = (sync, partySync, provider, contextItem) =>
    SyncItem.InterLookRich(
      Meta(
        id = partySync,
        meta1 = sync.map(_.x),
        meta2 = contextItem.value.some,
        meta5 = provider.x.some
      )
    )

  final val fromSync: SyncE => Option[CustomerGroupInterLookSync] =
    _.askInterLookRich.flatMap {
      case (dataId, m) =>
        Applicative[Option].map2(m.meta2, m.meta5) {
          case (ctx, provider) =>
            CustomerGroupInterLookSync(
              id = dataId.x,
              provider = provider,
              sync = m.meta1.map(LookCustomerGroupId(_)),
              partySync = m.id,
              context = ContextItem.withValue(ctx)
            )
        }
    }

  final val fromSyncItem: SyncItem => Option[LookCustomerGroupId] = {
    case SyncItem.InterLookRich(Meta(_, Some(id), _, _, _, _, _, _, _), _) =>
      LookCustomerGroupId(id).some
    case _ =>
      None
  }
}
