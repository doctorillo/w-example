package bookingtour.protocols.interlook.source.prices

import java.time.{Instant, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Ranges
import bookingtour.protocols.core.values.enumeration.ContextItem
import bookingtour.protocols.interlook.source.newTypes.{LookLinkServiceId, LookLinkServiceRef, LookOfferId}
import cats.instances.all._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class LinkServiceEP(
    id: LookLinkServiceId,
    offerId: LookOfferId,
    dataId: LookLinkServiceRef,
    ctx: ContextItem,
    duration: Ranges.Ints,
    stamp: Instant,
    canceled: Boolean
)

object LinkServiceEP {
  type Id = LookLinkServiceId

  implicit final val itemR: LinkServiceEP => Id = _.id

  implicit final val itemP: LinkServiceEP => LookOfferId = _.offerId

  final case class Output(
      id: Int,
      offerId: Int,
      dataId: Int,
      ctx: ContextItem,
      min: Option[Int],
      max: Option[Int],
      stamp: LocalDateTime,
      canceled: Option[LocalDateTime]
  )
  implicit final val outputTransform: Output => LinkServiceEP = _.into[LinkServiceEP]
    .withFieldComputed(_.id, x => LookLinkServiceId(x.id))
    .withFieldComputed(_.offerId, x => LookOfferId(x.offerId))
    .withFieldComputed(_.dataId, x => LookLinkServiceRef(x.dataId))
    .withFieldComputed(
      _.duration,
      x =>
        x.min
          .zip(x.max)
          .headOption
          .map(z => Ranges.Ints(z._1, z._2))
          .getOrElse(Ranges.Ints(3, 365))
    )
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .withFieldComputed(_.canceled, _.canceled.isDefined)
    .transform
}
