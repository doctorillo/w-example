package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{AppItem, LangItem}
import bookingtour.protocols.parties.newTypes.{AppId, SolverId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AppPREP(id: AppId, ident: AppItem, stamp: Instant)

object AppPREP {
  type Id = AppId

  implicit final val itemR0: AppPREP => Id = _.id

  implicit final val itemR1: AppPREP => Instant = _.stamp

  implicit final val itemP: AppPREP => Int = _ => 0

  final case class Output(id: UUID, ident: Int, stamp: LocalDateTime)

  implicit final val outputTransform: Output => AppPREP = _.into[AppPREP]
    .withFieldComputed(_.id, x => AppId(x.id))
    .withFieldComputed(_.ident, x => AppItem.withValue(x.ident))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      ident: AppItem,
      lang: List[LangItem],
      solverId: Option[SolverId] = None
  )

}
