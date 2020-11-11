package bookingtour.protocols.interlook.source.prices

import java.time.{Instant, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.interlook.source.newTypes.LookTariffId
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class TariffEP(
    id: LookTariffId,
    ctx: Option[ContextItem],
    name: String,
    notes: Option[String],
    stamp: Instant
)

object TariffEP {
  type Id = LookTariffId

  implicit final val itemR: TariffEP => Id = _.id

  implicit final val itemP: TariffEP => Int = _ => 0

  final case class Output(
      id: Int,
      ctx: Option[Int],
      name: String,
      notes: Option[String],
      created: LocalDateTime,
      updated: Option[LocalDateTime]
  )

  implicit final val outputTransform: Output => TariffEP = _.into[TariffEP]
    .withFieldComputed(_.id, x => LookTariffId(x.id))
    .withFieldComputed(_.ctx, _.ctx.map(ContextItem.withValue))
    .withFieldComputed(_.stamp, x => x.updated.getOrElse(x.created).toInstant(ZoneOffset.UTC))
    .transform
}
