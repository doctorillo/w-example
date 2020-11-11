package bookingtour.protocols.core.values.enumeration

import java.time.Instant
import java.util.UUID

import bookingtour.protocols.core._
import bookingtour.protocols.core.values.Amount
import cats.instances.all._
import cats.syntax.option._
import cats.syntax.order._
import derevo.cats.order
import derevo.circe.{decoder, encoder}
import derevo.derive
import io.circe.derivation._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor}
import tofu.logging.derivation.loggable

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, loggable)
sealed abstract class SyncItem(val source: SyncSourceItem) extends Product with Serializable

object SyncItem {
  @derive(encoder, decoder, order, loggable)
  final case class Meta(
      id: Int,
      meta1: Option[Int] = None,
      meta2: Option[Int] = None,
      meta3: Option[Int] = None,
      meta4: Option[Int] = None,
      meta5: Option[UUID] = None,
      meta6: Option[UUID] = None,
      meta7: Option[Amount] = None,
      stamp: Option[Instant] = None
  )

  @derive(order, loggable)
  final case class InterLook(
      id: Int,
      categoryId: Option[Int] = None,
      override val source: SyncSourceItem = SyncSourceItem.InterLook
  ) extends SyncItem(source)

  @derive(order, loggable)
  final case class InterLookRich(
      meta: Meta,
      override val source: SyncSourceItem = SyncSourceItem.InterLookRich
  ) extends SyncItem(source)

  @derive(order, loggable)
  final case class Operation(
      id: Int,
      override val source: SyncSourceItem = SyncSourceItem.PragueOperation
  ) extends SyncItem(source)

  @derive(order, loggable)
  final case class Parties(id: UUID, override val source: SyncSourceItem = SyncSourceItem.Parties)
      extends SyncItem(source)

  @derive(order, loggable)
  final case class Properties(
      id: UUID,
      override val source: SyncSourceItem = SyncSourceItem.Properties
  ) extends SyncItem(source)

  @derive(order, loggable)
  final case class InterLookAccommodationPrices(
      id: UUID,
      override val source: SyncSourceItem = SyncSourceItem.InterLookAccommodationPrices
  ) extends SyncItem(source)

  @derive(order, loggable)
  final case class InterLookOrders(
      id: UUID,
      override val source: SyncSourceItem = SyncSourceItem.InterLookOrders
  ) extends SyncItem(source)

  @derive(order, loggable)
  final case class Orders(id: UUID, override val source: SyncSourceItem = SyncSourceItem.Orders)
      extends SyncItem(source)

  implicit final val interLookSyncItemEnc: Encoder[InterLook]         = deriveEncoder
  implicit final val interLookSyncItemDec: Decoder[InterLook]         = deriveDecoder
  implicit final val interLookRichSyncItemEnc: Encoder[InterLookRich] = deriveEncoder
  implicit final val interLookRichSyncItemDec: Decoder[InterLookRich] = deriveDecoder
  implicit final val operationSyncItemEnc: Encoder[Operation]         = deriveEncoder
  implicit final val operationSyncItemDec: Decoder[Operation]         = deriveDecoder
  implicit final val partiesSyncItemEnc: Encoder[Parties]             = deriveEncoder
  implicit final val partiesSyncItemDec: Decoder[Parties]             = deriveDecoder
  implicit final val propertiesSyncItemEnc: Encoder[Properties]       = deriveEncoder
  implicit final val propertiesSyncItemDec: Decoder[Properties]       = deriveDecoder
  implicit final val interLookAccommodationPricesEnc: Encoder[
    InterLookAccommodationPrices
  ] = deriveEncoder
  implicit final val interLookAccommodationPricesDec: Decoder[
    InterLookAccommodationPrices
  ] = deriveDecoder
  implicit final val interLookLookOrdersItemEnc: Encoder[
    InterLookOrders
  ] = deriveEncoder
  implicit final val interLookOrdersItemDec: Decoder[
    InterLookOrders
  ]                                                     = deriveDecoder
  implicit final val ordersSyncItemEnc: Encoder[Orders] = deriveEncoder
  implicit final val ordersSyncItemDec: Decoder[Orders] = deriveDecoder
  implicit final val syncItemEnc: Encoder[SyncItem] = {
    case x: InterLook =>
      x.asJson
    case x: InterLookRich =>
      x.asJson
    case x: Operation =>
      x.asJson
    case x: Parties =>
      x.asJson
    case x: Properties =>
      x.asJson
    case x: InterLookAccommodationPrices =>
      x.asJson
    case x: InterLookOrders =>
      x.asJson
    case x: Orders =>
      x.asJson
  }
  implicit final val syncItemDec: Decoder[SyncItem] = (c: HCursor) => {
    val a = for {
      s <- c.downField("source").as[SyncSourceItem]
    } yield s
    a match {
      case Left(df) =>
        Left(df)
      case Right(x) =>
        x match {
          case SyncSourceItem.InterLook =>
            c.as[InterLook]
          case SyncSourceItem.InterLookRich =>
            c.as[InterLookRich]
          case SyncSourceItem.Parties =>
            c.as[Parties]
          case SyncSourceItem.Properties =>
            c.as[Properties]
          case SyncSourceItem.PragueOperation =>
            c.as[Operation]
          case SyncSourceItem.InterLookAccommodationPrices =>
            c.as[InterLookAccommodationPrices]
          case SyncSourceItem.InterLookOrders =>
            c.as[InterLookOrders]
          case SyncSourceItem.Orders =>
            c.as[Orders]
        }
    }
  }

  implicit final class SyncItemOps(private val self: SyncItem) extends AnyVal {
    def toInterLook: Option[SyncItem.InterLook] =
      if (self.source === SyncSourceItem.InterLook) {
        self.asInstanceOf[InterLook].some
      } else {
        none[SyncItem.InterLook]
      }

    def toInterLookRich: Option[SyncItem.InterLookRich] =
      if (self.source === SyncSourceItem.InterLookRich) {
        self.asInstanceOf[InterLookRich].some
      } else {
        none[SyncItem.InterLookRich]
      }
  }

  final val asInterLook: List[SyncItem] => List[SyncItem.InterLook] = xs =>
    xs.filter(_.source === SyncSourceItem.InterLook).map(_.asInstanceOf[SyncItem.InterLook])

  final val asInterLookRich: List[SyncItem] => List[SyncItem.InterLookRich] = xs =>
    xs.filter(_.source === SyncSourceItem.InterLookRich).map(_.asInstanceOf[SyncItem.InterLookRich])

  final val askInterLook: List[SyncItem] => Option[SyncItem.InterLook] = xs => asInterLook(xs).headOption

  final val askInterLookRich: List[SyncItem] => Option[SyncItem.InterLookRich] = xs => asInterLookRich(xs).headOption
}
