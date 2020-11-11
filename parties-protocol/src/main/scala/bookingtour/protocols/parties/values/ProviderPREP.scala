package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.parties.newTypes.{AppContextId, PartyId, ProviderId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class ProviderPREP(id: ProviderId, appContext: AppContextId, party: PartyId, stamp: Instant)

object ProviderPREP {
  type Id = ProviderId

  implicit final val itemR: ProviderPREP => Id = _.id

  implicit final val itemR1: ProviderPREP => Instant = _.stamp

  implicit final val itemP0: ProviderPREP => Int = _ => 0

  final case class Output(id: UUID, ctxId: UUID, partyId: UUID, stamp: LocalDateTime)

  implicit final val outputTransform: Output => ProviderPREP = _.into[ProviderPREP]
    .withFieldComputed(_.id, x => ProviderId(x.id))
    .withFieldComputed(_.appContext, x => AppContextId(x.ctxId))
    .withFieldComputed(_.party, x => PartyId(x.partyId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(ctxId: UUID, partyId: UUID, solverId: Option[UUID] = None)

  implicit final val itemP1: Create => Int = _ => 0
}
