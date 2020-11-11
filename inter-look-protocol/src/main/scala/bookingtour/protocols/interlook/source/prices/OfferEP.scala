package bookingtour.protocols.interlook.source.prices

import java.time.{Instant, LocalDateTime, ZoneOffset}

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.enumeration.{GoodsCategoryItem, OfferTypeItem}
import bookingtour.protocols.interlook.source.newTypes.LookOfferId
import cats.instances.all._
import cats.syntax.order._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.scalaland.chimney.dsl._

/**
  * © Alexey Toroshchin 2019.
  */
@derive(encoder, decoder, order)
final case class OfferEP(
    id: LookOfferId,
    goodsCategory: GoodsCategoryItem,
    offerType: OfferTypeItem,
    code: Option[String],
    hasRules: Boolean,
    beforeCheckIn: Boolean,
    stamp: Instant
)

object OfferEP {
  type Id = LookOfferId

  implicit final val itemR: OfferEP => Id = _.id

  implicit final val itemP: OfferEP => Int = _ => 0

  final case class Output(
      id: Int,
      goodsCategory: GoodsCategoryItem,
      offerType: OfferTypeItem,
      code: Option[String],
      hasRules: Boolean,
      beforeCheckIn: Option[Int],
      stamp: LocalDateTime
  )

  implicit final val outputTransform: Output => OfferEP = _.into[OfferEP]
    .withFieldComputed(_.id, x => LookOfferId(x.id))
    .withFieldComputed(_.beforeCheckIn, _.beforeCheckIn.exists(_ === 1))
    .withFieldComputed(_.stamp, _.stamp.toInstant(ZoneOffset.UTC))
    .transform
}
