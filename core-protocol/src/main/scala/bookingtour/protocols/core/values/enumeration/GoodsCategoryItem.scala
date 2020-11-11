package bookingtour.protocols.core.values.enumeration

import scala.collection.immutable

import enumeratum.values.{IntCirceEnum, IntEnum, IntEnumEntry}
import tofu.logging.derivation.loggable
import derevo.cats.order
import derevo.derive

/**
  * © Alexey Toroshchin 2019.
  */
@derive(order, loggable)
sealed abstract class GoodsCategoryItem(val value: Int, val context: ContextItem) extends IntEnumEntry

case object GoodsCategoryItem
    extends IntEnum[GoodsCategoryItem] with IntCirceEnum[GoodsCategoryItem] with LoggableIntEnum[GoodsCategoryItem] {
  override def values: immutable.IndexedSeq[GoodsCategoryItem] = findValues

  case object Accommodation extends GoodsCategoryItem(value = 1, context = ContextItem.Accommodation)

  case object AccommodationPenalty extends GoodsCategoryItem(value = 101, context = ContextItem.Accommodation)

  case object Transfer extends GoodsCategoryItem(value = 2, context = ContextItem.Transfer)

  case object TransferPenalty extends GoodsCategoryItem(value = 102, context = ContextItem.Transfer)

  case object Excursion extends GoodsCategoryItem(value = 4, context = ContextItem.Excursion)

  case object ExcursionPenalty extends GoodsCategoryItem(value = 104, context = ContextItem.Excursion)

  case object Extra extends GoodsCategoryItem(value = 5, context = ContextItem.Extra)

  case object ExtraPenalty extends GoodsCategoryItem(value = 105, context = ContextItem.Extra)

  case object Spa extends GoodsCategoryItem(value = 6, context = ContextItem.Spa)

  case object Transport extends GoodsCategoryItem(value = 7, context = ContextItem.Transport)

  implicit final val mapToContext: GoodsCategoryItem => ContextItem = {
    case GoodsCategoryItem.Accommodation =>
      ContextItem.Accommodation

    case GoodsCategoryItem.AccommodationPenalty =>
      ContextItem.Accommodation

    case GoodsCategoryItem.Excursion =>
      ContextItem.Excursion

    case GoodsCategoryItem.ExcursionPenalty =>
      ContextItem.Excursion

    case GoodsCategoryItem.Transfer =>
      ContextItem.Transfer

    case GoodsCategoryItem.TransferPenalty =>
      ContextItem.Transfer
      ContextItem.Excursion

    case GoodsCategoryItem.Extra =>
      ContextItem.Extra

    case GoodsCategoryItem.ExtraPenalty =>
      ContextItem.Extra

    case GoodsCategoryItem.Spa =>
      ContextItem.Accommodation

    case GoodsCategoryItem.Transport =>
      ContextItem.Transfer
  }
}
