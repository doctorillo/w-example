package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.LangItem
import bookingtour.protocols.parties.newTypes.{AppId, AppLangId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2020.
  */
@derive(encoder, decoder, order)
final case class AppLangPREP(
    id: AppLangId,
    appId: AppId,
    lang: LangItem,
    active: Boolean,
    stamp: Instant
)

object AppLangPREP {
  type Id = AppLangId

  implicit final val itemR0: AppLangPREP => Id = _.id

  implicit final val itemR1: AppLangPREP => Instant = _.stamp

  implicit final val itemP0: AppLangPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      appId: UUID,
      lang: Int,
      active: Boolean,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => AppLangPREP = _.into[AppLangPREP]
    .withFieldComputed(_.id, x => AppLangId(x.id))
    .withFieldComputed(_.appId, x => AppId(x.appId))
    .withFieldComputed(_.lang, x => LangItem.withValue(x.lang))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      appId: UUID,
      lang: Int,
      active: Boolean,
      solverId: Option[UUID] = None
  )

  final val create: UUID => List[Create] = x =>
    LangItem.values.toList.map(z => Create(appId = x, lang = z.value, active = true))

  implicit final val itemP1: Create => Int = _ => 0
}
