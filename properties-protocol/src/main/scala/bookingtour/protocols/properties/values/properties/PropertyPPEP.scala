package bookingtour.protocols.properties.values.properties

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.quantities.PropertyStar
import bookingtour.protocols.parties.newTypes.{PartyId}
import bookingtour.protocols.properties.newTypes.PropertyId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class PropertyPPEP(
    id: PropertyId,
    partyId: PartyId,
    editorPartyId: PartyId,
    name: String,
    star: PropertyStar,
    hasTreatment: Boolean,
    stamp: Instant
)

object PropertyPPEP {
  type Id = PropertyId

  implicit final val itemR0: PropertyPPEP => Id = _.id

  implicit final val itemR1: PropertyPPEP => Instant = _.stamp

  implicit final val itemP0: PropertyPPEP => PartyId = _.editorPartyId

  final case class Output(
      id: UUID,
      partyId: UUID,
      editorPartyId: UUID,
      name: String,
      star: Int,
      hasTreatment: Boolean,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => PropertyPPEP = _.into[PropertyPPEP]
    .withFieldComputed(_.id, x => PropertyId(x.id))
    .withFieldComputed(_.partyId, x => PartyId(x.partyId))
    .withFieldComputed(_.editorPartyId, x => PartyId(x.editorPartyId))
    .withFieldComputed(_.star, x => PropertyStar(x.star))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      partyId: UUID,
      editorPartyId: UUID,
      name: String,
      star: Int,
      hasTreatment: Boolean,
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0
}
