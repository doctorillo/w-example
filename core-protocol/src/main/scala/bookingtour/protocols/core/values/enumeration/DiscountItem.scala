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
sealed abstract class DiscountItem(val value: Int, val name: String) extends IntEnumEntry

case object DiscountItem
    extends IntEnum[DiscountItem] with IntCirceEnum[DiscountItem] with LoggableIntEnum[DiscountItem] {
  override def values: immutable.IndexedSeq[DiscountItem] = findValues

  case object Undefined extends DiscountItem(value = 0, name = "undefined")

  case object Package extends DiscountItem(value = 1, name = "package")

  case object Infant extends DiscountItem(value = 2, name = "infant")

  case object Child extends DiscountItem(value = 3, name = "child")

  case object Free extends DiscountItem(value = 4, name = "free")

  case object ByAgreement extends DiscountItem(value = 5, name = "by agreement")

  case object ChangePackage extends DiscountItem(value = 6, name = "change package")
}
