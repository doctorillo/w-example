package bookingtour.protocols.parties.values

import java.time.{Instant, LocalDateTime, ZoneOffset}
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId}
import bookingtour.protocols.parties.newTypes.{AppContextId, AppId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class AppContextPREP(
    id: AppContextId,
    appId: AppId,
    ctxType: ContextItem,
    code: String,
    stamp: Instant
)

object AppContextPREP {
  type Id = AppContextId

  implicit final val itemR0: AppContextPREP => Id = _.id

  implicit final val itemR1: AppContextPREP => Instant = _.stamp

  implicit final val itemP0: AppContextPREP => Int = _ => 0

  final case class Output(
      id: UUID,
      appId: UUID,
      contextItem: Int,
      code: String,
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => AppContextPREP = _.into[AppContextPREP]
    .withFieldComputed(_.id, x => AppContextId(x.id))
    .withFieldComputed(_.appId, x => AppId(x.appId))
    .withFieldComputed(_.ctxType, x => ContextItem.withValue(x.contextItem))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform

  @derive(order)
  final case class Create(
      appId: UUID,
      contextItem: ContextItem,
      code: String,
      solverId: Option[UUID] = None
  )

  implicit final val itemP1: Create => Int = _ => 0

  implicit final val itemT0: AppPREP => List[AppContextPREP.Create] = x =>
    ContextItem.values.toList.map(z => AppContextPREP.Create(appId = x.id.x, contextItem = z, code = z.name))
}
