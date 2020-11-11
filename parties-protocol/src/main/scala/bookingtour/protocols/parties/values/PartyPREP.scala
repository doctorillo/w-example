package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.db.SyncE
import bookingtour.protocols.core.values.enumeration.SyncItem
import bookingtour.protocols.interlook.source.newTypes.LookPartyId
import bookingtour.protocols.interlook.source.parties.PartnerEP
import bookingtour.protocols.parties.newTypes.PartyId
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
final case class PartyPREP(id: PartyId, stamp: Instant)

object PartyPREP {
  type Id = PartyId

  implicit final val itemR0: PartyPREP => Id = _.id

  implicit final val itemR1: PartyPREP => Instant = _.stamp

  implicit final val itemP0: PartyPREP => Int = _ => 0

  final case class Output(id: UUID, stamp: LocalDateTime)

  implicit final val outputTransform: Output => PartyPREP = _.into[PartyPREP]
    .withFieldComputed(_.id, x => PartyId(x.id))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(sync: SyncItem)

  implicit final val item1: Create => Int = _ => 0

  final case class CreateWithoutSync(code: String)

  implicit final val itemT0: PartnerEP => Create = x => Create(sync = SyncItem.InterLook(x.id.x))

  final case class PartyInterLookSync(id: PartyId, party: LookPartyId)

  final val toSync: LookPartyId => SyncItem.InterLook = x => SyncItem.InterLook(id = x)

  final val fromSync: SyncE => Option[PartyInterLookSync] =
    _.askInterLook.map(x => PartyInterLookSync(id = x._1.x, party = x._2))

  final val fromSyncItem: SyncItem => Option[LookPartyId] = {
    case SyncItem.InterLook(id, _, _) =>
      LookPartyId(id).some
    case _ =>
      none
  }

}
