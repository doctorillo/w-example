package bookingtour.protocols.core.values.db

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.newtypes.idents.{EnumId, EnumValue}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class EnumProjectionE(
    id: EnumId,
    valueId: EnumValue,
    stamp: Instant
)

object EnumProjectionE {
  type Id = EnumId

  implicit final val itemR0: EnumProjectionE => Id = _.id

  implicit final val itemR1: EnumProjectionE => Instant = _.stamp

  implicit final val itemP: EnumProjectionE => Int = _ => 0

  final case class Output(id: UUID, valueId: Int, stamp: LocalDateTime)

  implicit final val outputTransform: Output => EnumProjectionE = _.into[EnumProjectionE]
    .withFieldComputed(_.id, x => EnumId(x.id))
    .withFieldComputed(_.valueId, x => EnumValue(x.valueId))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(valueId: Int, solverId: Option[UUID] = None)

  implicit final val itemP1: Create => Int = _ => 0

  @derive(order)
  final case class CreateWithLabel(valueId: Int, name: String, solverId: Option[UUID] = None)

  implicit final val itemP2: CreateWithLabel => Int = _ => 0

  implicit final val itemT0: CreateWithLabel => Create = _.into[Create].transform
}
